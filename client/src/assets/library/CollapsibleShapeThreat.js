var CollapsibleShapeThreat={}
CollapsibleShapeThreat = draw2d.shape.layout.VerticalLayout.extend({

  NAME: "CollapsibleShape",

  init : function(attr,id)
  {

    this.inputLocator  = new CollapsibleInputLocator();
    this.outputLocator = new CollapsibleOutputLocator();

    this._super($.extend({bgColor:"#ffffff", color:"#39b2e5", stroke:1, radius:2, gap:5},attr));

    this.header = new draw2d.shape.layout.HorizontalLayout({
      stroke: 0,
      radius: 0,
      bgColor: "#1daeef"
    });

    var label = null;
    this.header.add(label =new draw2d.shape.basic.Label({
      text:"Collapsible Shape",
      fontColor:"#ffffff",
      stroke:0,
      fontSize:16,
      fontFamily:"Verdana",
      padding:{left:20, right:20},
      cssClass:"Header"
    }));

    label.setId(id);
    // var img1 = new draw2d.shape.icon.Contract({ minWidth:20, minHeight:20, width:20, height:20});
    // var img2 = new draw2d.shape.icon.Expand({  minWidth:20, minHeight:20, width:20, height:20, visible:false });

    var toggle=function(){

      this.row1.portRelayoutRequired=true;
      //  this.row2.portRelayoutRequired=true;

      this.row1.setVisible(!this.row1.isVisible());
      //  this.row2.setVisible(!this.row2.isVisible());

      this.row1.portRelayoutRequired=true;
      this.row1.layoutPorts();

      this.row2.portRelayoutRequired=true;
      //  this.row2.portRelayoutRequired=true;

      this.row2.setVisible(!this.row2.isVisible());
      //  this.row2.setVisible(!this.row2.isVisible());

      this.row2.portRelayoutRequired=true;
      this.row2.layoutPorts();

      this.row3.portRelayoutRequired=true;
      //  this.row2.portRelayoutRequired=true;

      this.row3.setVisible(!this.row3.isVisible());
      //  this.row2.setVisible(!this.row2.isVisible());

      this.row3.portRelayoutRequired=true;
      this.row3.layoutPorts();

      this.row4.portRelayoutRequired=true;
      //  this.row2.portRelayoutRequired=true;

      this.row4.setVisible(!this.row4.isVisible());
      //  this.row2.setVisible(!this.row2.isVisible());

      this.row4.portRelayoutRequired=true;
      this.row4.layoutPorts();

      //  this.row2.portRelayoutRequired=true;
      //  this.row2.layoutPorts();
      // img1.setVisible(!img1.isVisible());
      // img2.setVisible(!img2.isVisible());
    }.bind(this);
    //
    // img1.on("click",toggle);
    // img2.on("click",toggle);
    label.on("click",toggle);
    // img1.addCssClass("pointer");
    // img2.addCssClass("pointer");
    // this.header.add(img1);
    // this.header.add(img2);



    this.row1 = new draw2d.shape.basic.Label({text:"Threat List:", fontColor:"#303030",bold:true, resizeable:true, stroke:0, padding:{left:20}});
    //this.row2 = new draw2d.shape.basic.Label({text:"Attribute 2", fontColor:"#303030", resizeable:true, stroke:0, padding:{left:20}});

    this.row1.setVisible(false);

    this.row2 = new draw2d.shape.basic.Label({text:"", fontColor:"#303030", fontSize:11, resizeable:true, stroke:0, padding:{left:20}});
    //this.row2 = new draw2d.shape.basic.Label({text:"Attribute 2", fontColor:"#303030", resizeable:true, stroke:0, padding:{left:20}});

    this.row2.setVisible(false);

    this.row3 = new draw2d.shape.basic.Label({text:"Vulnerability List:", fontColor:"#303030", bold:true,resizeable:true, stroke:0, padding:{left:20}});
    //this.row2 = new draw2d.shape.basic.Label({text:"Attribute 2", fontColor:"#303030", resizeable:true, stroke:0, padding:{left:20}});

    this.row3.setVisible(false);

    this.row4 = new draw2d.shape.basic.Label({text:"", fontColor:"#303030",fontSize:11, resizeable:true, stroke:0, padding:{left:20}});
    //this.row2 = new draw2d.shape.basic.Label({text:"Attribute 2", fontColor:"#303030", resizeable:true, stroke:0, padding:{left:20}});

    this.row4.setVisible(false);
    this.row1.setId(id);
    this.row2.setId(id);
    this.row3.setId(id);
    this.row4.setId(id);
    //this.row2.setVisible(false)

    // this.row1.createPort("input",  this.inputLocator);
    // this.row1.createPort("output", this.outputLocator);
    //
    // this.row2.createPort("input",  this.inputLocator);
    // this.row2.createPort("output", this.outputLocator);

    // finally compose the shape with top/middle/bottom in VerticalLayout
    //
    this.add(this.header);
    this.add(this.row1);
    this.add(this.row2);
    this.add(this.row3);
    this.add(this.row4);
  }
});
