
/* templates */
var taskTemplateSource = document.getElementById("task-template").innerHTML;
var taskTemplate = Handlebars.compile(taskTemplateSource);

function datestr(time) {
	
    var date = moment(time).startOf('day');
    var today = moment().startOf('day');
    
	var days = date.diff(today, 'days');
	
	console.log(moment(date).format('l') +" - "+ moment().format('l') + " = "+days);
	
	var str = moment(date).format('l');
	if (days == 0) str = 'Today';
	if (days == 1) str = 'Tomorrow';
	if (days == -1) str = 'Yesterday';
	if (days < -1) str = 'Since '+days+' days';

	// expired ???
	return {
		expired : days <= 0,
		str : str
	}
}



var ListWrapper = function(id) {
	this.id = id;
	this.name = $(id).data("list");
	
	
	this.contextConfig  = {
		commandEdit  : true,
		commandCopy  : true,
		targetArchiv : this.name != "archiv",
		targetTrash  : this.name != "trash"
	};
	
	
	if(this.name == "trash")
	{
		this.contextConfig.commandEdit = false;
		this.contextConfig.targetArchiv = false;
		this.contextConfig.targetTrash = false;
	}
	
	if(this.name == "archiv")
	{
		this.contextConfig.commandEdit = false;
		this.contextConfig.targetArchiv = false;
	}
	
};

ListWrapper.prototype.GetName = function() {
	return this.name;
};


ListWrapper.prototype._Create = function(task,movecallback) {
	
	var currentList = this;

	
	var context = {
		id : task.id,
		title : task.title,
		duedate : datestr(task.duedate),
		config : this.contextConfig
	};
	
	var obj = $(taskTemplate(context));
	obj.find("button").click(function(ev) {
		ev.preventDefault();
	    
		var command = $(this).data("command");
		if(command != undefined)
		{
			console.log("command = "+command);
			
			var li = $(this).closest("li"); //todo use class
			if(command == "edit")
			{
				//open modal
				$('#editor').find('input[name="id"]').val(task.id);
				$('#editor').find('input[name="title"]').val(task.title);
				$('#editor').find('input[name="date"]').val(moment(task.duedate).format('l'));
				$('#editor').find('textarea[name="details"]').val(task.details);
				$('#editor').modal();
			}
			if(command == "copy")
			{
				//open modal
				$('#copier').find('input[name="id"]').val(task.id);
				$('#copier').find('.title').text(task.title);
				$('#copier').find('input[name="date"]').val(moment(new Date()).format('l'));
				$('#copier').modal();
			}
			return;
		}
		
		//move
		var target = $(this).data("target");
		console.log("move to = "+target);
		if(target == undefined) return;
		movecallback(currentList,task.id,target);
		
	});
	
	return obj;
	
	
}

ListWrapper.prototype.Add = function(task,movecallback) {

	var obj = this._Create(task,movecallback);
	obj.prependTo(this.id);

};

ListWrapper.prototype.Update = function(task,movecallback) {

	var obj = this._Create(task,movecallback);
	$("#"+task.id).replaceWith(obj);
	
};

