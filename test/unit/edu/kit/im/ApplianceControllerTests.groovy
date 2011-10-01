package edu.kit.im



import org.junit.*
import grails.test.mixin.*
import javax.servlet.http.HttpServletResponse

@TestFor(ApplianceController)
@Mock(Appliance)
class ApplianceControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/appliance/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.applianceInstanceList.size() == 0
        assert model.applianceInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.applianceInstance != null
    }

    void testSave() {
        controller.save()
        assert response.status == HttpServletResponse.SC_METHOD_NOT_ALLOWED

        response.reset()
        request.method = 'POST'
        controller.save()

        assert model.applianceInstance != null
        assert view == '/appliance/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/appliance/show/1'
        assert controller.flash.message != null
        assert Appliance.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/appliance/list'


        populateValidParams(params)
        def appliance = new Appliance(params)

        assert appliance.save() != null

        params.id = appliance.id

        def model = controller.show()

        assert model.applianceInstance == appliance
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/appliance/list'


        populateValidParams(params)
        def appliance = new Appliance(params)

        assert appliance.save() != null

        params.id = appliance.id

        def model = controller.edit()

        assert model.applianceInstance == appliance
    }

    void testUpdate() {

        controller.update()
        assert response.status == HttpServletResponse.SC_METHOD_NOT_ALLOWED

        response.reset()
        request.method = 'POST'
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/appliance/list'

        response.reset()


        populateValidParams(params)
        def appliance = new Appliance(params)

        assert appliance.save() != null

        // test invalid parameters in update
        params.id = appliance.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/appliance/edit"
        assert model.applianceInstance != null

        appliance.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/appliance/show/$appliance.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        appliance.clearErrors()

        populateValidParams(params)
        params.id = appliance.id
        params.version = -1
        controller.update()

        assert view == "/appliance/edit"
        assert model.applianceInstance != null
        assert model.applianceInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert response.status == HttpServletResponse.SC_METHOD_NOT_ALLOWED

        response.reset()
        request.method = 'POST'
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/appliance/list'

        response.reset()

        populateValidParams(params)
        def appliance = new Appliance(params)

        assert appliance.save() != null
        assert Appliance.count() == 1

        params.id = appliance.id

        controller.delete()

        assert Appliance.count() == 0
        assert Appliance.get(appliance.id) == null
        assert response.redirectedUrl == '/appliance/list'
    }
}
