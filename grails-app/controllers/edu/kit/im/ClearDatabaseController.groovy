package edu.kit.im

import org.hibernate.Query

class ClearDatabaseController {

    def sessionFactory

    def index = {

        sessionFactory.getCurrentSession().createQuery("delete from Consumption").executeUpdate()
        render ("ok!")
    }
}
