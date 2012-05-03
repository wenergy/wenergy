databaseChangeLog = {

	changeSet(author: "ddauer (generated)", id: "1336032666486-1") {
		addColumn(tableName: "event") {
			column(name: "duration", type: "varchar(255)")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1336032666486-2") {
		addColumn(tableName: "event") {
			column(name: "parameters", type: "varchar(255)")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1336032666486-3") {
		addColumn(tableName: "event") {
			column(name: "url", type: "varchar(255)")
		}
	}
}
