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

	changeSet(author: "ddauer (generated)", id: "1335185245192-1") {
		createTable(tableName: "api_error") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "api_errorPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "datetime") {
				constraints(nullable: "false")
			}

			column(name: "household_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "ip_address", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "type", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "ddauer (generated)", id: "1335185245192-2") {
		createIndex(indexName: "FKFAC133A3B9AF2C40", tableName: "api_error") {
			column(name: "household_id")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1335185245192-3") {
		addForeignKeyConstraint(baseColumnNames: "household_id", baseTableName: "api_error", constraintName: "FKFAC133A3B9AF2C40", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "household", referencesUniqueColumn: "false")
	}
}
