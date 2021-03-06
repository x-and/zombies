#version 120

uniform sampler2D tex0;
uniform float resolution;
uniform float radius;

void main() {
	vec4 sum = vec4(0.0);
	vec2 tc = gl_TexCoord[0].st;
	float blur = radius/resolution; 
	
	sum += texture2D(tex0, vec2(tc.x - 4.0*blur, tc.y)) * 0.05;
	sum += texture2D(tex0, vec2(tc.x - 3.0*blur, tc.y)) * 0.09;
	sum += texture2D(tex0, vec2(tc.x - 2.0*blur, tc.y)) * 0.12;
	sum += texture2D(tex0, vec2(tc.x - 1.0*blur, tc.y)) * 0.15;
	
	sum += texture2D(tex0, vec2(tc.x, tc.y)) * 0.16;
	
	sum += texture2D(tex0, vec2(tc.x + 1.0*blur, tc.y)) * 0.15;
	sum += texture2D(tex0, vec2(tc.x + 2.0*blur, tc.y)) * 0.12;
	sum += texture2D(tex0, vec2(tc.x + 3.0*blur, tc.y)) * 0.09;
	sum += texture2D(tex0, vec2(tc.x + 4.0*blur, tc.y)) * 0.05;
	

	gl_FragColor = vec4(sum.rgba);
}