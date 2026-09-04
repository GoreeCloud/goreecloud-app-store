mod catalog;
mod ui;

use adw::prelude::*;
use gtk::glib;

const APP_ID: &str = "com.goreecloud.AppStore.Development";

fn main() -> glib::ExitCode {
    let app = adw::Application::builder().application_id(APP_ID).build();
    app.connect_activate(|app| match catalog::Catalog::embedded() {
        Ok(catalog) => ui::build(app, catalog),
        Err(error) => show_catalog_error(app, &error.to_string()),
    });
    app.run()
}

fn show_catalog_error(app: &adw::Application, detail: &str) {
    let window = adw::ApplicationWindow::builder()
        .application(app)
        .title("GoreeCloud App Store Dev")
        .default_width(560)
        .default_height(260)
        .build();

    let content = gtk::Box::new(gtk::Orientation::Vertical, 12);
    content.set_margin_top(32);
    content.set_margin_bottom(32);
    content.set_margin_start(32);
    content.set_margin_end(32);

    let title = gtk::Label::new(Some("The development catalog could not be trusted"));
    title.set_xalign(0.0);
    title.add_css_class("title-2");
    content.append(&title);

    let message = gtk::Label::new(Some(
        "The Linux App Store fails closed instead of presenting partial or unvalidated catalog data.",
    ));
    message.set_xalign(0.0);
    message.set_wrap(true);
    content.append(&message);

    let technical = gtk::Label::new(Some(detail));
    technical.set_xalign(0.0);
    technical.set_wrap(true);
    technical.add_css_class("dim-label");
    content.append(&technical);

    window.set_content(Some(&content));
    window.present();
}
