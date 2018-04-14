var ListService = function (url) {
    this.url = url;
};





ListService.prototype.List = function (listwrapper) {
	
    var movecallback = this.Move;

	$.ajax({
            type: "POST",
            url: this.url,
            data: { function : "list" , list : listwrapper.GetName() },
            success: function (tasklist) {
            	
            	$.each(tasklist , function(index,task) {
            		listwrapper.Add(task,movecallback);
            	});
            },
            error: function (msg) {
                console.log(msg);
            }
	});
}

ListService.prototype.Create = function (listwrapper,title) {

    var movecallback = this.Move;

	$.ajax({
        type: "POST",
        url: this.url,
        data: { function : "create" , list : listwrapper.GetName() , title : title },
        success: function (task) {
    		listwrapper.Add(task,movecallback);
        },
        error: function (msg) {
            console.log(msg);
        }
   });
}

ListService.prototype.Move = function (listwrapper, id , target) {
	

	$.ajax({
        type: "POST",
        url: this.url,
        data: { function : "move" , list : listwrapper.GetName() , id : id , target : target},
        success: function (id) {
            console.log(id);
        	$('#'+id).slideUp(200,function() {
        		 $(this).remove();
        	});
        },
        error: function (msg) {
            console.log(msg);
        }
   });
}

ListService.prototype.Update = function (listwrapper, values ) {
	
    var movecallback = this.Move;

	values['list'] = listwrapper.GetName();
	values['function'] = "update";

	console.log(values);
	
	$.ajax({
        type: "POST",
        url: this.url,
        data: jQuery.param(values),
        success: function (task) {
    		listwrapper.Update(task,movecallback);
        },
        error: function (msg) {
            console.log(msg);
        }
   });
}


ListService.prototype.Copy = function (listwrapper, values ) {
	
    var movecallback = this.Move;

	values['list'] = listwrapper.GetName();
	values['function'] = "copy";

	var addcurrent = values['list'] == values['target'];
	
	console.log(values);
	
	$.ajax({
        type: "POST",
        url: this.url,
        data: jQuery.param(values),
        success: function (task) {
        	
        	if(addcurrent)
        		listwrapper.Add(task,movecallback);
        },
        error: function (msg) {
            console.log(msg);
        }
   });
}