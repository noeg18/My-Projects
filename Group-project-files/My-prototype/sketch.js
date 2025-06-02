let m, t;
let p = [];
let space = 32;

function setup() {
  createCanvas(600, 600);
  t = new tank();
}

function draw(){
  
  background(220);
  m = new map();
  t.move();
  t.drawTank();
  this.x = t.getOriginX();
  this.y = t.getOriginY();
  this.angle = t.getAngle();
  lines = m.getLines();
  for(let i = 0; i < p.length; i++){
      p[i].moveProj();
      p[i].show();
    for(let j = 0; j < lines.length; j++){
      p[i].ricochet(lines[j].x1, lines[j].y1, lines[j].x2, lines[j].y2);
    }
  }

}

function keyPressed(){
  if(keyCode === space){
    let bullet = new projectile(this.x, this.y, this.angle);
    p.push(bullet);
  }
}