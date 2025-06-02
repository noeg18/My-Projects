let l = [];
let i = 0;

class map {
  
  constructor(){
    stroke('black');
    strokeWeight(6);
    fill(220);
    square(10, 10, 580);
    
    this.drawLine(100, 10, 100, 300);
    this.drawLine(100, 100, 300, 100);
    this.drawLine(400, 10, 400, 200);
    this.drawLine(300, 300, 590, 300);
    this.drawLine(500, 200, 500, 300);
    this.drawLine(200, 100, 200, 400);
    this.drawLine(100, 400, 200, 400);
    this.drawLine(400, 300, 400, 500);
    this.drawLine(300, 500, 500, 500);
    this.drawLine(300, 400, 300, 500);
    this.drawLine(10, 500, 200, 500);
    this.drawLine(500, 100, 590, 100);
  }
  
  drawLine(x1, y1, x2, y2){
    
    line(x1, y1, x2, y2);
    
    l.push({x1, y1, x2, y2});
  }
  
  getLines(){
    return l;
  }
  
  
}