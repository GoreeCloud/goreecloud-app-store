#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)"
VERSION="${1:-0.1.5~dev1}"
ARCH="${2:-amd64}"
BINARY="${3:-$ROOT/linux/target/release/goreecloud-app-store-dev}"
OUT="${4:-$ROOT/.artifacts/GoreeCloud-App-Store-${VERSION}-${ARCH}.deb}"

if [[ ! -x "$BINARY" ]]; then
  echo "Missing Linux App Store binary: $BINARY" >&2
  exit 1
fi

WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT
PKG="$WORK/package"
mkdir -p "$PKG/DEBIAN" "$PKG/usr/bin" "$PKG/usr/share/applications" "$PKG/usr/share/metainfo" "$PKG/usr/share/goreecloud-app-store/catalog" "$(dirname "$OUT")"

install -m 0755 "$BINARY" "$PKG/usr/bin/goreecloud-app-store-dev"
install -m 0644 "$ROOT/linux/resources/com.goreecloud.AppStore.Development.desktop" "$PKG/usr/share/applications/com.goreecloud.AppStore.Development.desktop"
install -m 0644 "$ROOT/linux/resources/com.goreecloud.AppStore.Development.metainfo.xml" "$PKG/usr/share/metainfo/com.goreecloud.AppStore.Development.metainfo.xml"
install -m 0644 "$ROOT/catalog/development-catalog.json" "$PKG/usr/share/goreecloud-app-store/catalog/development-catalog.json"

cat > "$PKG/DEBIAN/control" <<EOF
Package: goreecloud-app-store-dev
Version: $VERSION
Section: utils
Priority: optional
Architecture: $ARCH
Maintainer: GoreeCloud <295260680+GoreeCloud@users.noreply.github.com>
Depends: libgtk-4-1 (>= 4.6), libadwaita-1-0 (>= 1.1)
Description: GoreeCloud App Store development Linux client
 Native GTK 4/libadwaita development client for browsing the entitlement-filtered
 GoreeCloud application/service catalog and Linux package publication state.
 Production acceptance is false.
EOF

chmod 0755 "$PKG/DEBIAN"
dpkg-deb --build --root-owner-group "$PKG" "$OUT"
sha256sum "$OUT"
