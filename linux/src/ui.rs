use crate::catalog::{development_sessions, ArtifactStatus, Catalog, IdentitySession, StoreItem, StoreItemType};
use adw::prelude::*;
use gtk::{gdk, gio};
use std::cell::RefCell;
use std::rc::Rc;

#[derive(Clone, Copy, Debug, PartialEq, Eq)]
enum Section {
    Discover,
    Apps,
    Services,
    Packages,
}

#[derive(Debug)]
struct ViewState {
    section: Section,
    identity_index: usize,
    query: String,
}

pub fn build(app: &adw::Application, catalog: Catalog) {
    install_glaze_css();

    let catalog = Rc::new(catalog);
    let sessions = Rc::new(development_sessions());
    let state = Rc::new(RefCell::new(ViewState {
        section: Section::Discover,
        identity_index: 0,
        query: String::new(),
    }));

    let window = adw::ApplicationWindow::builder()
        .application(app)
        .title("GoreeCloud App Store Dev")
        .default_width(1120)
        .default_height(760)
        .build();

    let root = gtk::Box::new(gtk::Orientation::Vertical, 0);
    let header = adw::HeaderBar::new();
    header.add_css_class("glaze-header");

    let title = gtk::Box::new(gtk::Orientation::Vertical, 0);
    let brand = gtk::Label::new(Some("GoreeCloud"));
    brand.set_xalign(0.0);
    brand.add_css_class("caption");
    brand.add_css_class("dim-label");
    let product = gtk::Label::new(Some("App Store"));
    product.set_xalign(0.0);
    product.add_css_class("title-3");
    title.append(&brand);
    title.append(&product);
    header.set_title_widget(Some(&title));

    let share_button = gtk::Button::with_label("Share catalog");
    share_button.add_css_class("flat");
    if catalog.share_url.is_empty() {
        share_button.set_sensitive(false);
        share_button.set_tooltip_text(Some("No public catalog URL is configured yet"));
    } else {
        let share_url = catalog.share_url.clone();
        share_button.connect_clicked(move |_| {
            if let Some(display) = gdk::Display::default() {
                display.clipboard().set_text(&share_url);
            }
        });
    }
    header.pack_end(&share_button);

    let session_names: Vec<&str> = sessions.iter().map(|session| session.display_name).collect();
    let session_model = gtk::StringList::new(&session_names);
    let identity_selector = gtk::DropDown::builder()
        .model(&session_model)
        .selected(0)
        .build();
    identity_selector.set_tooltip_text(Some("Development identity fixture"));
    header.pack_end(&identity_selector);
    root.append(&header);

    let body = gtk::Box::new(gtk::Orientation::Horizontal, 0);
    body.set_vexpand(true);

    let navigation_shell = gtk::Box::new(gtk::Orientation::Vertical, 14);
    navigation_shell.set_width_request(220);
    navigation_shell.set_margin_top(18);
    navigation_shell.set_margin_bottom(18);
    navigation_shell.set_margin_start(18);
    navigation_shell.set_margin_end(10);
    navigation_shell.add_css_class("glaze-navigation");

    let environment = gtk::Label::new(Some("DEVELOPMENT"));
    environment.set_xalign(0.0);
    environment.add_css_class("caption-heading");
    environment.add_css_class("accent");
    navigation_shell.append(&environment);

    let navigation = gtk::ListBox::new();
    navigation.set_selection_mode(gtk::SelectionMode::Single);
    navigation.add_css_class("navigation-sidebar");
    for label in ["Discover", "Apps", "Services", "Linux packages"] {
        let row = gtk::ListBoxRow::new();
        let text = gtk::Label::new(Some(label));
        text.set_xalign(0.0);
        text.set_margin_top(10);
        text.set_margin_bottom(10);
        text.set_margin_start(12);
        text.set_margin_end(12);
        row.set_child(Some(&text));
        navigation.append(&row);
    }
    if let Some(first) = navigation.row_at_index(0) {
        navigation.select_row(Some(&first));
    }
    navigation_shell.append(&navigation);

    let boundary = gtk::Label::new(Some(
        "Package downloads remain fail-closed until release metadata, signing/provenance, and Wardveil acceptance are present.",
    ));
    boundary.set_wrap(true);
    boundary.set_xalign(0.0);
    boundary.add_css_class("caption");
    boundary.add_css_class("dim-label");
    navigation_shell.append(&boundary);
    body.append(&navigation_shell);

    let main = gtk::Box::new(gtk::Orientation::Vertical, 14);
    main.set_hexpand(true);
    main.set_vexpand(true);
    main.set_margin_top(18);
    main.set_margin_bottom(18);
    main.set_margin_start(10);
    main.set_margin_end(18);

    let search = gtk::SearchEntry::new();
    search.set_placeholder_text(Some("Search your available GoreeCloud catalog"));
    search.add_css_class("glaze-search");
    main.append(&search);

    let scroll = gtk::ScrolledWindow::builder()
        .hexpand(true)
        .vexpand(true)
        .hscrollbar_policy(gtk::PolicyType::Never)
        .build();
    let content = gtk::Box::new(gtk::Orientation::Vertical, 18);
    content.set_margin_bottom(24);
    scroll.set_child(Some(&content));
    main.append(&scroll);
    body.append(&main);
    root.append(&body);
    window.set_content(Some(&root));

    render_catalog(&content, &catalog, &sessions, &state.borrow());

    {
        let catalog = Rc::clone(&catalog);
        let sessions = Rc::clone(&sessions);
        let state = Rc::clone(&state);
        let content = content.clone();
        navigation.connect_row_selected(move |_, row| {
            let Some(row) = row else { return };
            state.borrow_mut().section = match row.index() {
                1 => Section::Apps,
                2 => Section::Services,
                3 => Section::Packages,
                _ => Section::Discover,
            };
            render_catalog(&content, &catalog, &sessions, &state.borrow());
        });
    }

    {
        let catalog = Rc::clone(&catalog);
        let sessions = Rc::clone(&sessions);
        let state = Rc::clone(&state);
        let content = content.clone();
        identity_selector.connect_selected_notify(move |selector| {
            let selected = selector.selected() as usize;
            if selected < sessions.len() {
                state.borrow_mut().identity_index = selected;
                render_catalog(&content, &catalog, &sessions, &state.borrow());
            }
        });
    }

    {
        let catalog = Rc::clone(&catalog);
        let sessions = Rc::clone(&sessions);
        let state = Rc::clone(&state);
        let content = content.clone();
        search.connect_search_changed(move |entry| {
            state.borrow_mut().query = entry.text().to_string();
            render_catalog(&content, &catalog, &sessions, &state.borrow());
        });
    }

    window.present();
}

fn install_glaze_css() {
    let Some(display) = gdk::Display::default() else {
        return;
    };
    let provider = gtk::CssProvider::new();
    provider.load_from_data(include_str!("../resources/style.css"));
    gtk::StyleContext::add_provider_for_display(
        &display,
        &provider,
        gtk::STYLE_PROVIDER_PRIORITY_APPLICATION,
    );
}

fn render_catalog(
    content: &gtk::Box,
    catalog: &Catalog,
    sessions: &[IdentitySession],
    state: &ViewState,
) {
    while let Some(child) = content.first_child() {
        content.remove(&child);
    }

    let session = &sessions[state.identity_index.min(sessions.len() - 1)];
    let visible = catalog.visible_items(session);
    let filtered: Vec<&StoreItem> = visible
        .into_iter()
        .filter(|item| match state.section {
            Section::Discover | Section::Packages => true,
            Section::Apps => item.item_type == StoreItemType::Application,
            Section::Services => item.item_type == StoreItemType::Service,
        })
        .filter(|item| item.matches_query(&state.query))
        .collect();

    let intro = gtk::Box::new(gtk::Orientation::Vertical, 5);
    let heading = gtk::Label::new(Some(match state.section {
        Section::Discover => "Discover",
        Section::Apps => "Applications",
        Section::Services => "Services",
        Section::Packages => "Linux packages",
    }));
    heading.set_xalign(0.0);
    heading.add_css_class("title-1");
    intro.append(&heading);

    let subtitle = match state.section {
        Section::Packages => format!(
            "{} visible products · Debian and Flatpak publication state",
            filtered.len()
        ),
        _ => format!(
            "{} available to {} · catalog filtered before presentation",
            filtered.len(),
            session.display_name
        ),
    };
    let subtitle_label = gtk::Label::new(Some(&subtitle));
    subtitle_label.set_xalign(0.0);
    subtitle_label.add_css_class("dim-label");
    intro.append(&subtitle_label);
    content.append(&intro);

    if !catalog.authoritative {
        let notice = gtk::Label::new(Some(&catalog.notice));
        notice.set_wrap(true);
        notice.set_xalign(0.0);
        notice.set_margin_top(2);
        notice.set_margin_bottom(2);
        notice.set_margin_start(14);
        notice.set_margin_end(14);
        notice.add_css_class("development-notice");
        content.append(&notice);
    }

    if filtered.is_empty() {
        let empty = gtk::Box::new(gtk::Orientation::Vertical, 8);
        empty.set_margin_top(48);
        let title = gtk::Label::new(Some("Nothing available here"));
        title.add_css_class("title-2");
        let detail = gtk::Label::new(Some(
            "This view only shows catalog entries authorized for the active development identity and matching your search.",
        ));
        detail.set_wrap(true);
        detail.set_justify(gtk::Justification::Center);
        detail.add_css_class("dim-label");
        empty.append(&title);
        empty.append(&detail);
        content.append(&empty);
        return;
    }

    let flow = gtk::FlowBox::new();
    flow.set_selection_mode(gtk::SelectionMode::None);
    flow.set_column_spacing(16);
    flow.set_row_spacing(16);
    flow.set_min_children_per_line(1);
    flow.set_max_children_per_line(2);
    flow.set_homogeneous(true);

    for item in filtered {
        flow.insert(&store_card(item, state.section == Section::Packages), -1);
    }
    content.append(&flow);
}

fn store_card(item: &StoreItem, package_focus: bool) -> gtk::Box {
    let card = gtk::Box::new(gtk::Orientation::Vertical, 12);
    card.set_width_request(360);
    card.set_hexpand(true);
    card.set_margin_top(2);
    card.set_margin_bottom(2);
    card.set_margin_start(2);
    card.set_margin_end(2);
    card.add_css_class("store-card");

    let title_row = gtk::Box::new(gtk::Orientation::Horizontal, 10);
    let name = gtk::Label::new(Some(&item.name));
    name.set_xalign(0.0);
    name.set_hexpand(true);
    name.set_wrap(true);
    name.add_css_class("title-3");
    title_row.append(&name);

    let type_badge = gtk::Label::new(Some(match item.item_type {
        StoreItemType::Application => "APP",
        StoreItemType::Service => "SERVICE",
    }));
    type_badge.add_css_class("status-pill");
    title_row.append(&type_badge);
    card.append(&title_row);

    let metadata = gtk::Label::new(Some(&format!("{} · {}", item.category, item.release_channel)));
    metadata.set_xalign(0.0);
    metadata.add_css_class("caption");
    metadata.add_css_class("dim-label");
    card.append(&metadata);

    let summary = gtk::Label::new(Some(&item.summary));
    summary.set_xalign(0.0);
    summary.set_wrap(true);
    summary.set_lines(3);
    card.append(&summary);

    if !item.version.is_empty() || !item.package_name.is_empty() || !item.service_url.is_empty() {
        let details = gtk::Label::new(Some(&format!(
            "Version: {}{}{}",
            if item.version.is_empty() { "unknown" } else { &item.version },
            if item.package_name.is_empty() { "" } else { " · package identity available" },
            if item.service_url.is_empty() { "" } else { " · service endpoint available" },
        )));
        details.set_xalign(0.0);
        details.set_wrap(true);
        details.add_css_class("caption");
        details.add_css_class("dim-label");
        card.append(&details);
    }

    let package_label = gtk::Label::new(Some(if package_focus {
        "Download formats"
    } else {
        "Linux availability"
    }));
    package_label.set_xalign(0.0);
    package_label.add_css_class("heading");
    card.append(&package_label);

    for artifact in &item.artifacts.linux {
        card.append(&artifact_row(artifact));
    }

    card
}

fn artifact_row(artifact: &crate::catalog::LinuxArtifact) -> gtk::Box {
    let row = gtk::Box::new(gtk::Orientation::Horizontal, 10);
    row.add_css_class("artifact-row");

    let labels = gtk::Box::new(gtk::Orientation::Vertical, 2);
    labels.set_hexpand(true);
    let format = gtk::Label::new(Some(artifact.label()));
    format.set_xalign(0.0);
    format.add_css_class("heading");
    labels.append(&format);

    let detail = format!(
        "{} · {} · {}",
        artifact.architecture,
        artifact.role,
        artifact.status_label()
    );
    let detail_label = gtk::Label::new(Some(&detail));
    detail_label.set_xalign(0.0);
    detail_label.set_wrap(true);
    detail_label.add_css_class("caption");
    detail_label.add_css_class(if artifact.status == ArtifactStatus::Published && artifact.is_download_ready() {
        "success"
    } else {
        "dim-label"
    });
    labels.append(&detail_label);
    row.append(&labels);

    let button = if artifact.is_download_ready() {
        let button = gtk::Button::with_label("Download");
        button.add_css_class("suggested-action");
        let url = artifact.download_url.clone();
        button.connect_clicked(move |_| {
            if let Err(error) = gio::AppInfo::launch_default_for_uri(
                &url,
                None::<&gio::AppLaunchContext>,
            ) {
                eprintln!("Unable to open package download: {error}");
            }
        });
        button
    } else {
        let button = gtk::Button::with_label(artifact.status_label());
        button.set_sensitive(false);
        button
    };
    row.append(&button);
    row
}
