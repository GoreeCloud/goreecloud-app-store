use serde::Deserialize;
use std::collections::HashSet;

const EMBEDDED_CATALOG: &str = include_str!("../../catalog/development-catalog.json");

#[derive(Debug, Clone, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct Catalog {
    pub schema_version: u32,
    pub environment: String,
    pub authoritative: bool,
    pub share_url: String,
    pub notice: String,
    pub items: Vec<StoreItem>,
}

#[derive(Debug, Clone, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct StoreItem {
    pub id: String,
    pub name: String,
    pub summary: String,
    #[serde(rename = "type")]
    pub item_type: StoreItemType,
    pub category: String,
    pub version: String,
    pub release_channel: String,
    pub package_name: String,
    pub service_url: String,
    pub artifacts: ArtifactSet,
    pub access: AccessRule,
}

#[derive(Debug, Clone, Copy, Deserialize, PartialEq, Eq)]
#[serde(rename_all = "lowercase")]
pub enum StoreItemType {
    Application,
    Service,
}

#[derive(Debug, Clone, Deserialize)]
pub struct ArtifactSet {
    pub linux: Vec<LinuxArtifact>,
}

#[derive(Debug, Clone, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct LinuxArtifact {
    pub format: ArtifactFormat,
    pub role: String,
    pub status: ArtifactStatus,
    pub architecture: String,
    pub package_id: String,
    pub download_url: String,
    pub sha256: String,
    pub source_revision: String,
    pub signed: bool,
    pub wardveil_accepted: bool,
}

#[derive(Debug, Clone, Copy, Deserialize, PartialEq, Eq, Hash)]
#[serde(rename_all = "lowercase")]
pub enum ArtifactFormat {
    Deb,
    Flatpak,
}

#[derive(Debug, Clone, Copy, Deserialize, PartialEq, Eq)]
#[serde(rename_all = "lowercase")]
pub enum ArtifactStatus {
    Unpublished,
    Published,
    Blocked,
    Withdrawn,
}

#[derive(Debug, Clone, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct AccessRule {
    pub require_signed_in: bool,
    pub any_audience: Vec<String>,
}

#[derive(Debug, Clone)]
pub struct IdentitySession {
    pub display_name: &'static str,
    pub audiences: HashSet<&'static str>,
    pub authenticated: bool,
}

impl IdentitySession {
    pub fn can_access_release_channel(&self, release_channel: &str) -> bool {
        if !self.authenticated {
            return false;
        }
        let claim = match release_channel.to_ascii_lowercase().as_str() {
            "stable" => "channel:stable",
            "rc" => "channel:rc",
            "beta" => "channel:beta",
            "development" => "channel:development",
            "debug" => "channel:debug",
            _ => return false,
        };
        self.audiences.contains(claim)
    }
}

impl Catalog {
    pub fn embedded() -> Result<Self, serde_json::Error> {
        serde_json::from_str(EMBEDDED_CATALOG)
    }

    pub fn visible_items<'a>(&'a self, session: &IdentitySession) -> Vec<&'a StoreItem> {
        self.items
            .iter()
            .filter(|item| item.is_visible_to(session))
            .collect()
    }
}

impl StoreItem {
    pub fn is_visible_to(&self, session: &IdentitySession) -> bool {
        if self.access.require_signed_in && !session.authenticated {
            return false;
        }
        let audience_allowed = self.access.any_audience.is_empty()
            || self
                .access
                .any_audience
                .iter()
                .any(|audience| session.audiences.contains(audience.as_str()));
        audience_allowed && session.can_access_release_channel(&self.release_channel)
    }

    pub fn matches_query(&self, query: &str) -> bool {
        let query = query.trim().to_lowercase();
        query.is_empty()
            || self.name.to_lowercase().contains(&query)
            || self.summary.to_lowercase().contains(&query)
            || self.category.to_lowercase().contains(&query)
    }
}

impl LinuxArtifact {
    pub fn label(&self) -> &'static str {
        match self.format {
            ArtifactFormat::Deb => "Debian (.deb)",
            ArtifactFormat::Flatpak => "Flatpak",
        }
    }

    pub fn is_download_ready(&self) -> bool {
        self.status == ArtifactStatus::Published
            && self.download_url.starts_with("https://")
            && self.sha256.len() == 64
            && self.sha256.chars().all(|c| c.is_ascii_hexdigit())
            && self.source_revision.len() == 40
            && self.source_revision.chars().all(|c| c.is_ascii_hexdigit())
            && !self.package_id.is_empty()
            && self.signed
            && self.wardveil_accepted
    }

    pub fn status_label(&self) -> &'static str {
        match self.status {
            ArtifactStatus::Unpublished => "Not published yet",
            ArtifactStatus::Published if self.is_download_ready() => "Ready to download",
            ArtifactStatus::Published => "Published metadata incomplete",
            ArtifactStatus::Blocked => "Blocked",
            ArtifactStatus::Withdrawn => "Withdrawn",
        }
    }
}

pub fn development_sessions() -> Vec<IdentitySession> {
    vec![
        IdentitySession {
            display_name: "Standard demo",
            audiences: HashSet::from(["audience:standard", "channel:stable"]),
            authenticated: true,
        },
        IdentitySession {
            display_name: "Preview tester demo",
            audiences: HashSet::from([
                "audience:standard",
                "channel:stable",
                "channel:beta",
                "channel:rc",
            ]),
            authenticated: true,
        },
        IdentitySession {
            display_name: "Administrator demo",
            audiences: HashSet::from([
                "audience:standard",
                "audience:administrator",
                "channel:stable",
            ]),
            authenticated: true,
        },
        IdentitySession {
            display_name: "Developer demo",
            audiences: HashSet::from([
                "audience:standard",
                "audience:developer",
                "channel:stable",
                "channel:rc",
                "channel:beta",
                "channel:development",
                "channel:debug",
            ]),
            authenticated: true,
        },
        IdentitySession {
            display_name: "Signed out",
            audiences: HashSet::new(),
            authenticated: false,
        },
    ]
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn embedded_catalog_is_non_authoritative_development_data() {
        let catalog = Catalog::embedded().expect("embedded catalog should parse");
        assert_eq!(catalog.schema_version, 2);
        assert_eq!(catalog.environment, "development-fixture");
        assert!(!catalog.authoritative);
    }

    #[test]
    fn release_channel_claims_are_explicit_and_fail_closed() {
        let sessions = development_sessions();
        let standard = &sessions[0];
        let preview = &sessions[1];
        let developer = &sessions[3];
        assert!(standard.can_access_release_channel("stable"));
        assert!(!standard.can_access_release_channel("development"));
        assert!(preview.can_access_release_channel("beta"));
        assert!(preview.can_access_release_channel("rc"));
        assert!(!preview.can_access_release_channel("debug"));
        assert!(developer.can_access_release_channel("debug"));
        assert!(!developer.can_access_release_channel("unknown"));
    }

    #[test]
    fn administrator_has_no_implicit_channel_or_audience_bypass() {
        let catalog = Catalog::embedded().expect("embedded catalog should parse");
        let sessions = development_sessions();
        let administrator = &sessions[2];
        let manager = catalog
            .items
            .iter()
            .find(|item| item.id == "goreecloud.manager")
            .expect("manager fixture exists");
        assert!(!manager.is_visible_to(administrator));

        let developer_only = StoreItem {
            id: "test.developer-only".into(),
            name: "Developer only".into(),
            summary: "Test".into(),
            item_type: StoreItemType::Application,
            category: "Test".into(),
            version: "development".into(),
            release_channel: "stable".into(),
            package_name: String::new(),
            service_url: String::new(),
            artifacts: ArtifactSet { linux: vec![] },
            access: AccessRule {
                require_signed_in: true,
                any_audience: vec!["audience:developer".into()],
            },
        };
        assert!(!developer_only.is_visible_to(administrator));
    }

    #[test]
    fn developer_sees_development_entries_permitted_by_audience() {
        let catalog = Catalog::embedded().expect("embedded catalog should parse");
        let sessions = development_sessions();
        let developer = &sessions[3];
        let visible = catalog.visible_items(developer);
        assert!(visible.iter().any(|item| item.id == "goreecloud.browser"));
        assert!(visible.iter().any(|item| item.id == "goreecloud.mesh-center"));
        assert!(!visible.iter().any(|item| item.id == "goreecloud.manager"));
    }

    #[test]
    fn every_catalog_item_has_debian_and_flatpak_slots() {
        let catalog = Catalog::embedded().expect("embedded catalog should parse");
        for item in &catalog.items {
            let formats: HashSet<_> = item.artifacts.linux.iter().map(|a| a.format).collect();
            assert_eq!(
                formats,
                HashSet::from([ArtifactFormat::Deb, ArtifactFormat::Flatpak])
            );
            assert!(item
                .artifacts
                .linux
                .iter()
                .all(|artifact| !artifact.is_download_ready()));
        }
    }
}
