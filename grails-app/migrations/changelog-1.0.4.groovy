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

  changeSet(author: "ddauer (generated)", id: "1335266596767-1") {
		addColumn(tableName: "household") {
			column(name: "theme", type: "varchar(255)")
		}
	}

	changeSet(author: "ddauer (generated)", id: "1335266596767-2") {
		sql("update household set theme = 'wenergy'")
	}

	changeSet(author: "ddauer (generated)", id: "1335266596767-3") {
		addNotNullConstraint(columnDataType: "varchar(255)", columnName: "theme", tableName: "household")
	}
}
