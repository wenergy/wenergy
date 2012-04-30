class UrlMappings {

  static mappings = {

    "/"(controller: "home")

    "/admin/console"(controller: "console")

    "/$controller/$action?/$id?" {
      constraints {
        controller(notEqual: "console")
        // apply constraints here
      }
    }

    "500"(view: '/error')
  }
}
