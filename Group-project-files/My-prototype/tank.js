let minDistance = 0;

class tank{

  constructor(){
    this.originX = 100;  
    this.originY = 430;   // no significance, just a starting  point for now
    this.angle = 0;
    this.speed = 2;
    this.angleIncrement = 0.05;
    
  }
  
  drawTank(){
    
    push();
    noStroke();
    fill(0, 0, 150);
    rectMode(CENTER);
    translate(this.originX, this.originY);
    rotate(this.angle);
    rect(0, 0, 60, 50);
    pop();
    
    push();
    stroke('black');
    fill(0, 0, 150);
    rectMode(CENTER);
    translate(this.originX, this.originY);
    rotate(this.angle);
    circle(0, 0, 20);
    pop();
    
    push();
    fill(0, 0, 150);
    stroke('black');
    rectMode(CORNER);
    translate(this.originX, this.originY);
    rotate(this.angle);
    rect(0, -5, 40, 10); // adjust by width / 2
    pop();

  }
  
  move(){
    
    if(keyIsDown(UP_ARROW)){
      this.moveUpDown(this.speed);
      
    }
    
    if(keyIsDown(DOWN_ARROW)){
     this.moveUpDown(-this.speed);
    }
    
    if(keyIsDown(RIGHT_ARROW)){
      this.angle += this.angleIncrement;
    }
    
    if(keyIsDown(LEFT_ARROW)){
      this.angle -= this.angleIncrement;
    }
  }
 
  getOriginX(){
    return this.originX;
  }
  
  getOriginY(){
    return this.originY;
  }
  
  moveUpDown(speed){
    this.originX += speed * cos(this.angle);
    this.originY += speed * sin(this.angle);
  }
  
  getAngle(){
    return this.angle;
  }
  
  
}