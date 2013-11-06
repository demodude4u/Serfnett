float f = 100.0;
float d = 100.0;

varies float speed;

void main(void) {
	vec4 pos = ftransform();
    gl_FrontColor = gl_Color;
    
    vec4 twisted = vec4(pos.x+sin(pos.y*f)/d,pos.y+sin(pos.x*f)/d,pos.z,pos.w);
    
    gl_Position = twisted;
    //gl_Position = pos;
    
    //gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    //gl_Position = gl_Position * gl_Position;
}
