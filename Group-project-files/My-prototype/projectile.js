let vertical = true;

class projectile{
  
  constructor(x, y, angle){
    this.x = x;
    this.y = y;
    this.angle = angle;
    this.size = 10;
    this.speed = 3;
  }
  
  show(){
    fill(0, 0, 150);
    circle(this.x, this.y, this.size);
  }
  
  moveProj(){
    
   
    this.x += this.speed * cos(this.angle);
    this.y += this.speed * sin(this.angle);
    
  }
   
   
  ricochet(x1, y1, x2, y2){
    
     if(x1 === x2){
       vertical = true;
     }
     else{
       vertical = false;
     }
     
     if(vertical == true){
       if(this.x < x1 + this.size && this.x > x1 - this.size && this.y > y1 && this.y < y2){
          this.speed = -this.speed;
          }
     }
     else{
       if(this.y < y1 + this.size && this.y > y1 - this.size && this.x > x1 && this.x < x2){
         this.speed = -this.speed
       }
     }
     
    
}
  
}