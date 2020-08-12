var CollapsibleShape = {}
CollapsibleShape = draw2d.shape.layout.VerticalLayout.extend({

  NAME: "CollapsibleShape",

  init: function (attr, id) {

    this.inputLocator = new CollapsibleInputLocator();
    this.outputLocator = new CollapsibleOutputLocator();

    this._super($.extend({bgColor: "#ffffff", color: "#39b2e5", stroke: 1, radius: 2, gap: 5}, attr));
    this.header = new draw2d.shape.layout.HorizontalLayout({
      stroke: 0,
      radius: 0,
      bgColor: "#1daeef"
    });

    var label = null;
    this.header.add(label = new draw2d.shape.basic.Label({
      text: "Collapsible Shape",
      fontColor: "#ffffff",
      stroke: 0,
      fontSize: 16,
      fontFamily: "Verdana",
      padding: {left: 20, right: 20},
      cssClass: "Header"
    }));

    label.setId(id);
    var toggle = function () {

      this.row1.portRelayoutRequired = true;
      this.row1.setVisible(!this.row1.isVisible());
      this.row1.portRelayoutRequired = true;
      this.row1.layoutPorts();
    }.bind(this);

    label.on("click", toggle);

    this.row1 = new draw2d.shape.basic.Label({
      text: "",
      fontColor: "#303030",
      resizeable: true,
      stroke: 0,
      padding: {left: 20}
    });
    this.row1.setVisible(false);

    this.row1.setId(id);
    this.add(this.header);
    this.add(this.row1);
  }
});

