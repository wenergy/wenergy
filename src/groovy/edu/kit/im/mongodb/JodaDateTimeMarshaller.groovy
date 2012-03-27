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

package edu.kit.im.mongodb

import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import org.grails.datastore.mapping.engine.types.AbstractMappingAwareCustomTypeMarshaller
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.mongo.query.MongoQuery
import org.grails.datastore.mapping.query.Query
import org.joda.time.DateTime

class JodaDateTimeMarshaller extends AbstractMappingAwareCustomTypeMarshaller<DateTime, DBObject, DBObject> {

  JodaDateTimeMarshaller() {
    super(DateTime)
  }

  @Override
  protected Object writeInternal(PersistentProperty property, String key, DateTime value, DBObject nativeTarget) {
    // https://github.com/grails/grails-core/blob/master/grails-hibernate/src/main/groovy/org/codehaus/groovy/grails/orm/hibernate/support/ClosureEventListener.java
    // Insert value just like hibernate does
    if (value == null && key == "dateCreated") {
      value = new DateTime()
    }
    final converted = value.toDate()
    nativeTarget.put(key, converted)
    return converted
  }

  @Override
  protected void queryInternal(PersistentProperty property, String key, Query.PropertyCriterion value, DBObject nativeQuery) {
//    System.out.println("prop " + property + " key " + key + " crit " + value + " native " + nativeQuery)
    if (value instanceof Query.Between) {
      def dbo = new BasicDBObject()
      dbo.put(MongoQuery.MONGO_GTE_OPERATOR, value.getFrom().toDate());
      dbo.put(MongoQuery.MONGO_LTE_OPERATOR, value.getTo().toDate());
      nativeQuery.put(key, dbo)
    }
    else if (value instanceof Query.LessThan) {
      def dbo = new BasicDBObject()
      dbo.put(MongoQuery.MONGO_LT_OPERATOR, value.value.toDate());
      nativeQuery.put(key, dbo)
    }
    else if (value instanceof Query.LessThanEquals) {
      def dbo = new BasicDBObject()
      dbo.put(MongoQuery.MONGO_LTE_OPERATOR, value.value.toDate());
      nativeQuery.put(key, dbo)
    }
    else if (value instanceof Query.GreaterThan) {
      def dbo = new BasicDBObject()
      dbo.put(MongoQuery.MONGO_GT_OPERATOR, value.value.toDate());
      nativeQuery.put(key, dbo)
    }
    else if (value instanceof Query.GreaterThanEquals) {
      def dbo = new BasicDBObject()
      dbo.put(MongoQuery.MONGO_GTE_OPERATOR, value.value.toDate());
      nativeQuery.put(key, dbo)
    }
    else {
      nativeQuery.put(key, value.value.toDate())
    }
  }

  @Override
  protected DateTime readInternal(PersistentProperty property, String key, DBObject nativeSource) {
    final num = nativeSource.get(key)
    if (num instanceof Date) {
      return new DateTime(num)
    }
    return null
  }
}
