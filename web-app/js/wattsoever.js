/**
 * Created by IntelliJ IDEA.
 * User: tteubner
 * Date: 29.09.11
 * Time: 14:08
 * To change this template use File | Settings | File Templates.
 */


$(document).ready(function() {



    $.ajax({
        url: "consumption/dailyData",
        method: 'GET',
        dataType: 'json',
        success: onDailyDataReceived
    });

    function onDailyDataReceived (data) {

    }



})
