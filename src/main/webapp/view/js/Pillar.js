// 在JQUERY基础上，编写的柱状图类
//feature:
//1.柱状图的各种柱形类型、颜色、名称及总数量均可自定义
//2.柱形图所用的坐标系也能自定义
//3.坐标系上最多可显示多少组柱状图也可自定义
//4.柱形图载入方式有一次性全部载入和单步载入两种，而单步载入时，又可根据条件判断是否移除最左边（时间最早的）那条组柱状图。
//5.数据载入的方法分为本地载入JSON和远程载入JSON两种，而远程载入时，则采用JSONP方式，这样就可实现跨域

//定义一个柱状图类：
function PillarMap(bgDiv,maxLevel,minLevel,step,stepPy,coorWidth,intervalGroup){
//初始化柱状图的对象
	this.$bgDiv=bgDiv;													//最父框架的DIV
	this.$contentDiv=bgDiv.children(".zhu_coor").children(".zhu_content");//储放柱状图内容的DIV
	this.$maxLevel=maxLevel;												//定义最高的点,建议步进数的整数倍
	this.$minLevel=minLevel;												//定义最小的点
	this.$step=step;														//定义柱状图背景每一行的步进数
	this.$stepPy=stepPy;													//定义每一行步进时，屏幕显示的像素高度
	//柱状图背景坐标系的总高度
	this.$coorHeight=((maxLevel-minLevel)/step)*stepPy+20; 
	this.$coorWidth=coorWidth;											/*柱状图背景坐标系的宽度*/
	this.$notice=null;													//数据种类的详细
	this.$kindNum=0;														//数据种类数量
	this.$textWidth=80;													//每组数据文字说明所在DIV的宽度,默认为80
	this.$kuangWidth=0;													//每组数据的各个柱状图连在一起后的总宽度
	this.$maxDataLimit=0;												//设定柱状图表坐标系中，最多保持多少组数据，默认：0为不限制，可直接修改
	this.$dataSize=0;													//现有数据组的数量；初始为0，用户可根据需要来加减
	this.$intervalGroup=intervalGroup;									//每个数据组之间的间隔,默认为20
	
	
	//初始化柱状图的标题及单位说明
	this.initTitle=function(title,units){
		
		var Label=this.$bgDiv.children(".zhu_title");
		if(title!=null){
			//初始化柱状图的标题JQUERY对象
			Label.children(".title").html(title);
			//初始化 柱状图的单位提示JQUERY对象
			Label.children(".unit").html("　单位:("+units+")");
		}
	}

//初始化柱状图的坐标系
	this.initCoorAndGrage=function(){
		bgCoor=this.$bgDiv.children(".zhu_coor");//坐标图的DIV对象
		coorGrade=this.$bgDiv.children(".coor_grade");
		bgCoor.height(this.$coorHeight);
		bgCoor.width(this.$coorWidth);
		var lineNum=(this.$maxLevel-this.$minLevel)/this.$step;
		bgCoor.append("<span style='border:0px;height:20px'/>");
		var coorHtml="";
		var gradeHtml=""
		for(i=0;i<lineNum;++i){
			coorHtml+="<span style='height:"+(this.$stepPy-1)+"px'/>";
			gradeHtml+="<span style='margin-bottom:"+(this.$stepPy-12)+"px'>"+(this.$maxLevel-i*this.$step)+"</span>";
		}
		bgCoor.append(coorHtml);
		coorGrade.append(gradeHtml+"<span>"+this.$minLevel+"</span>");
	};
	
//初始化柱状图的数据种类数量
	this.setDataKind=function(dataKind,noteWide){
		//设置提示框的宽度
		this.$notice=dataKind;
		noticeDiv=this.$bgDiv.children(".zhu_note").css({width:noteWide+"px"});
		if(noticeDiv!=null){
			var noticeHtml="";
			for(key=0;key<dataKind.length;key++){
				noticeHtml+="<span class='icon' style='background-color:"+dataKind[key].color+"'></span>"
						+"<span>:"+dataKind[key].label+"</span>";
			}
			noticeDiv.append(noticeHtml);
		}
		this.$kindNum=dataKind.length;
		this.$kuangWidth=20*this.$kindNum;//柱状图宽度一律为20PX，不可再改
		this.$textWidth = this.$textWidth<=(this.$kuangWidth)? this.$kuangWidth+10 : this.$textWidth;//设定文字说明所在SPAN的宽度
	};
	//也可以通过远程读取JS文件来获得dataKind然后再初始化(pillarMap为一个Pillar类的对象)
	this.setDataKindFromJs=function(pillarMap,url,noteWide){
		$.getScript(url,function(){
			pillarMap.setDataKind(dataKind,noteWide);
		}); 
	};

//正式一次性载入JSON数据的方法：
	this.loadJson=function(data){
		for(i=0;i<data.length;++i){
			var simpleData=data[i];
			this.loadStepSimpleData(simpleData,false);
		}
	};

//采用步进方式（如每组数据都是不同时间段且定时更新出来的数据）单步载入单条数据的方法
//clearFirst为BOOL型变量，它决定在步进方式读取单组数据时，只有当this.maxDataLimit>0时这个项才有意义,且坐标中的数据数组已经达到了maxDataLimit,是否把柱状图中原有数据中的左边第一条最原始数据删除
	this.loadStepSimpleData=function(simpleData,clearFirst){
		var pillar=$("<div class='pillar' style='width:"+this.$textWidth+"px;margin-left:"+this.$intervalGroup+"px'></div>");
		var kuangInner="<div class='kuang' style='width:"+this.$kuangWidth+"px'>";
		for(key=0;key<this.$notice.length;key++){
			var Item=simpleData.items[key];
			var height=Math.round(((Item-this.$minLevel)/this.$step)*this.$stepPy);
			kuangInner+="<div class='item'><div class='tip' style='bottom:"+height+"px'>"+Item+"</div><div class='pool' style='height:"+height+"px;background:"+this.$notice[key].color+"'></div></div>";
		}
		kuangInner+="</div>"
		pillar.append(kuangInner);
		pillar.append("<span style='width:"+this.$textWidth+"px'><div class='px'/>"+simpleData.text+"</span>");
		this.$contentDiv.append(pillar);
		++this.$dataSize;
		if(clearFirst){
			if(this.$maxDataLimit>0 && this.$dataSize>this.$maxDataLimit){
				this.$contentDiv.children(".pillar:first-child").remove();
				--this.$dataSize;
			}
		}
	};

//远程用JSONP实现跨域读取单步进JSON数据的方法(pillarMap为一个Pillar类的对象)
	this.jsonpLoadStepSimpleData=function(pillarMap,url,param,clearFirst){
		$.getJSON(url+"?jsonp=?",param,function(simpleData){
			pillarMap.loadStepSimpleData(simpleData,clearFirst);
		});
	};

//远程用JSONP实现跨域读取一次性所有JSON数据的方法(pillarMap为一个Pillar类的对象)
	this.jsonpLoadJson=function(pillarMap,url,param){
		$.getJSON(url+"?jsonp=?",param,function(data){
			pillarMap.loadJson(data);
		});
	};
	
//清除坐标中所有的柱状图组
	this.clearContent=function(){
		this.$contentDiv.children(".pillar").remove();
	};
//删除坐标中第N个柱状图组
	this.delGroupByIndex=function(n){
		if(n>0&&n<this.$dataSize){
			this.$contentDiv.children(".pillar:eq("+(n-1)+")").remove();
		}
		else
			alert("索引数值n超出范围!");
	};
}

//将此类的构造函数加入至JQUERY对象中
jQuery.extend({
	createPillar: function(bgDiv,maxLevel,minLevel,step,stepPy,coorWidth,intervalGroup) {
		return new PillarMap(bgDiv,maxLevel,minLevel,step,stepPy,coorWidth,intervalGroup);
  }
}); 
