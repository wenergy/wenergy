class UrlMappings {

  static mappings = {

    "/"(controller: "home")

    "/data"(controller: "data")
    "/data/$interval?"(controller: "data", action: "data") {
      constraints {
        interval(inList: ["daily", "daily15", "weekly", "monthly"])
      }
    }

    "/$controller/$action?/$id?" {
      constraints {
        // apply constraints here
      }
    }

    "500"(view: '/error')
  }
}
