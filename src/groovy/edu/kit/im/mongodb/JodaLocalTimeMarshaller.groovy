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
import org.joda.time.LocalTime

// Based on
// http://usertype.svn.sourceforge.net/viewvc/usertype/trunk/usertype.core/src/main/java/org/jadira/usertype/dateandtime/joda/columnmapper/LongColumnLocalTimeMapper.java?revision=248&view=markup

class JodaLocalTimeMarshaller extends AbstractMappingAwareCustomTypeMarshaller<LocalTime, DBObject, DBObject> {

  JodaLocalTimeMarshaller() {
    super(LocalTime)
  }

  @Override
  protected Object writeInternal(PersistentProperty property, String key, LocalTime value, DBObject nativeTarget) {
    final converted = Long.valueOf(value.getMillisOfDay() * 1000000L)
    nativeTarget.put(key, converted)
    return converted
  }

  @Override
  protected void queryInternal(PersistentProperty property, String key, Query.PropertyCriterion value, DBObject nativeQuery) {
    if (value instanceof Query.Between) {
      def dbo = new BasicDBObject()
      dbo.put(MongoQuery.MONGO_GTE_OPERATOR, Long.valueOf(value.getFrom().getMillisOfDay() * 1000000L));
      dbo.put(MongoQuery.MONGO_LTE_OPERATOR, Long.valueOf(value.getTo().getMillisOfDay() * 1000000L));
      nativeQuery.put(key, dbo)
    }
    else {
      nativeQuery.put(key, Long.valueOf(value.value.getMillisOfDay() * 1000000L))
    }
  }

  @Override
  protected LocalTime readInternal(PersistentProperty property, String key, DBObject nativeSource) {
    final num = nativeSource.get(key)
    if (num instanceof Long) {
      Long converted = (num / 1000000L) as Long
      return LocalTime.fromMillisOfDay(converted)
    }
    return null
  }
}
