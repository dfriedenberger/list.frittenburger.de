



$(document).ready(function () {

	moment.locale('de');
	
    $('.datetimepicker').datetimepicker({
        locale: 'de',
        format: 'l'
    });

    var listservice = new ListService("/api/v1");
    var currentList = new ListWrapper("#tasklist");
    
    //modal
    $(".save").click(function(ev) {
    	ev.preventDefault();
    	
    	var valuesArray = $("#editor").find("form").serializeArray();
    	
        var values = {};

        $(valuesArray).each(function(i, field){
        	values[field.name] = field.value;
        });
    	
    	var a = moment(values['date'],'l');
    	values['date'] = a.toDate().getTime();
    	
    	listservice.Update(currentList,values);
    	$("#editor").modal('hide');
    });
    
    $(".copy").click(function(ev) {
    	ev.preventDefault();
    	
    	var valuesArray = $("#copier").find("form").serializeArray();
    	
        var values = {};

        $(valuesArray).each(function(i, field){
        	values[field.name] = field.value;
        });
    	
    	var a = moment(values['date'],'l');
    	values['date'] = a.toDate().getTime();
    	
    	listservice.Copy(currentList,values);
    	$("#copier").modal('hide');
    });
    
    
    
    //search / add input
	$("#addtask").keyup(function(ev) {
		var keyCode = ev.which;
		//console.log("keyCode = "+keyCode);
		if(keyCode != 13) return; // not enter

		var query = $(this).val().trim();
		$(this).val("");
		if(query == "") return;
		listservice.Create(currentList,query);
	});
	
	//get items from current list
	listservice.List(currentList);
	
})