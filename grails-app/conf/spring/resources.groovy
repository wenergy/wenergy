import edu.kit.im.mongodb.JodaDateTimeMarshaller
import edu.kit.im.mongodb.JodaLocalTimeMarshaller

// Spring beans
beans = {
  jodaDateTimeMarshaller(JodaDateTimeMarshaller)
  jodaLocalTimeMarshaller(JodaLocalTimeMarshaller)
}
