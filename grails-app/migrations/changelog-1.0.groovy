/*
 * Copyright 2011-2012 Institute of Information Engineering and Management,
 * Information & Market Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

databaseChangeLog = {

	changeSet(author: "ddauer (generated)", id: "1333988364616-1") {
		createTable(tableName: "aggregated_consumption") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "aggregated_coPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "avg_power_phase1", type: "decimal(19,3)") {
				constraints(nullable: "false")
			}

			column(name: "avg_power_phase2", type: "decimal(19,3)") {
				constraints(nullable: "false")
			}

			column(name: "avg_power_phase3", type: "decimal(19,3)") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "datetime") {
				constraints(nullable: "false")
			}

			column(name: "day_of_month", type: "integer") {
				constraints(nullable: "false")
			}

			column(name: "day_of_week", type: "integer") {
				constraints(nullable: "false")
			}

			column(name: "household_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "interval_end", type: "datetime") {
				constraints(nullable: "false")
			}

			column(name: "interval_end_time", type: "time") {
				constraints(nullable: "false")
			}

			column(name: "interval_start", type: "datetime") {
				constraints(nullable: "false")
			}

			column(name: "interval_start_time", type: "time") {
				constraints(nullable: "false")
			}

			column(name: "sum_power_phase1", type: "decimal(19,3)") {
				constraints(nullable: "false")
			}

			column(name: "sum_power_phase2", type: "decimal(19,3)") {
				constraints(nullable: "false")
			}

			column(name: "sum_power_phase3", type: "decimal(19,3)") {
				constraints(nullable: "false")
			}

			column(name: "type", type: "integer") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-2") {
		createTable(tableName: "aggregated_consumption_consumption") {
			column(name: "aggregated_consumption_consumptions_id", type: "bigint")

			column(name: "consumption_id", type: "bigint")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-3") {
		createTable(tableName: "consumption") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "consumptionPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "battery_level", type: "decimal(19,3)") {
				constraints(nullable: "false")
			}

			column(name: "date", type: "datetime") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "datetime") {
				constraints(nullable: "false")
			}

			column(name: "household_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "power_phase1", type: "decimal(19,3)") {
				constraints(nullable: "false")
			}

			column(name: "power_phase2", type: "decimal(19,3)") {
				constraints(nullable: "false")
			}

			column(name: "power_phase3", type: "decimal(19,3)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-4") {
		createTable(tableName: "household") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "householdPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "account_expired", type: "bit") {
				constraints(nullable: "false")
			}

			column(name: "account_locked", type: "bit") {
				constraints(nullable: "false")
			}

			column(name: "address", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "city", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "datetime") {
				constraints(nullable: "false")
			}

			column(name: "device_id", type: "bigint") {
				constraints(unique: "true")
			}

			column(name: "e_mail", type: "varchar(255)") {
				constraints(nullable: "false", unique: "true")
			}

			column(name: "enabled", type: "bit") {
				constraints(nullable: "false")
			}

			column(name: "full_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "password", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "password_expired", type: "bit") {
				constraints(nullable: "false")
			}

			column(name: "peergroup_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "reference_consumption_value", type: "decimal(19,2)")

			column(name: "username", type: "varchar(255)") {
				constraints(nullable: "false", unique: "true")
			}

			column(name: "zip_code", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-5") {
		createTable(tableName: "household_role") {
			column(name: "role_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "household_id", type: "bigint") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-6") {
		createTable(tableName: "peergroup") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "peergroupPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "datetime") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false", unique: "true")
			}
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-7") {
		createTable(tableName: "role") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "rolePK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "authority", type: "varchar(255)") {
				constraints(nullable: "false", unique: "true")
			}
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-8") {
		addPrimaryKey(columnNames: "role_id, household_id", constraintName: "household_rolPK", tableName: "household_role")
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-9") {
		createIndex(indexName: "FKF3D86CE1B9AF2C40", tableName: "aggregated_consumption") {
			column(name: "household_id")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-10") {
		createIndex(indexName: "intervalStart_idx", tableName: "aggregated_consumption") {
			column(name: "interval_start")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-11") {
		createIndex(indexName: "intervalStart_intervalEnd_type_idx", tableName: "aggregated_consumption") {
			column(name: "interval_end")

			column(name: "interval_start")

			column(name: "type")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-12") {
		createIndex(indexName: "intervalStart_type_idx", tableName: "aggregated_consumption") {
			column(name: "interval_start")

			column(name: "type")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-13") {
		createIndex(indexName: "FK801D753D1278E4E0", tableName: "aggregated_consumption_consumption") {
			column(name: "aggregated_consumption_consumptions_id")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-14") {
		createIndex(indexName: "FK801D753DE782140", tableName: "aggregated_consumption_consumption") {
			column(name: "consumption_id")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-15") {
		createIndex(indexName: "FKCD71F39BB9AF2C40", tableName: "consumption") {
			column(name: "household_id")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-16") {
		createIndex(indexName: "date_idx", tableName: "consumption") {
			column(name: "date")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-17") {
		createIndex(indexName: "FK3DA62BDF7216B380", tableName: "household") {
			column(name: "peergroup_id")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-18") {
		createIndex(indexName: "device_id_unique_1333988364495", tableName: "household", unique: "true") {
			column(name: "device_id")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-19") {
		createIndex(indexName: "e_mail_unique_1333988364496", tableName: "household", unique: "true") {
			column(name: "e_mail")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-20") {
		createIndex(indexName: "username_unique_1333988364497", tableName: "household", unique: "true") {
			column(name: "username")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-21") {
		createIndex(indexName: "FK3C75A0D61A5F2354", tableName: "household_role") {
			column(name: "role_id")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-22") {
		createIndex(indexName: "FK3C75A0D6B9AF2C40", tableName: "household_role") {
			column(name: "household_id")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-23") {
		createIndex(indexName: "name_unique_1333988364500", tableName: "peergroup", unique: "true") {
			column(name: "name")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-24") {
		createIndex(indexName: "authority_unique_1333988364500", tableName: "role", unique: "true") {
			column(name: "authority")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-25") {
		addForeignKeyConstraint(baseColumnNames: "household_id", baseTableName: "aggregated_consumption", constraintName: "FKF3D86CE1B9AF2C40", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "household", referencesUniqueColumn: "false")
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-26") {
		addForeignKeyConstraint(baseColumnNames: "aggregated_consumption_consumptions_id", baseTableName: "aggregated_consumption_consumption", constraintName: "FK801D753D1278E4E0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "aggregated_consumption", referencesUniqueColumn: "false")
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-27") {
		addForeignKeyConstraint(baseColumnNames: "consumption_id", baseTableName: "aggregated_consumption_consumption", constraintName: "FK801D753DE782140", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "consumption", referencesUniqueColumn: "false")
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-28") {
		addForeignKeyConstraint(baseColumnNames: "household_id", baseTableName: "consumption", constraintName: "FKCD71F39BB9AF2C40", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "household", referencesUniqueColumn: "false")
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-29") {
		addForeignKeyConstraint(baseColumnNames: "peergroup_id", baseTableName: "household", constraintName: "FK3DA62BDF7216B380", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "peergroup", referencesUniqueColumn: "false")
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-30") {
		addForeignKeyConstraint(baseColumnNames: "household_id", baseTableName: "household_role", constraintName: "FK3C75A0D6B9AF2C40", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "household", referencesUniqueColumn: "false")
	}

	changeSet(author: "ddauer (generated)", id: "1333988364616-31") {
		addForeignKeyConstraint(baseColumnNames: "role_id", baseTableName: "household_role", constraintName: "FK3C75A0D61A5F2354", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "role", referencesUniqueColumn: "false")
	}
}
