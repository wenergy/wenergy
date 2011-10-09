class UrlMappings {

  static mappings = {

    "/"(controller: "home")

    "/data"(controller: "data")

    "/data/$range?"(controller: "data", action: "data") {
      constraints {
        range(inList:["daily", "weekly", "monthly"])
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
