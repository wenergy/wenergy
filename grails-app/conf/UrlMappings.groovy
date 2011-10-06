class UrlMappings {

  static mappings = {

    "/"(controller: "home")

    "/data/$action?/$id?/$option?"(controller: "data")

    "/$controller/$action?/$id?" {
      constraints {
        // apply constraints here
      }
    }

    "500"(view: '/error')
  }
}
