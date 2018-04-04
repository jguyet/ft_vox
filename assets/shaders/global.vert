#version 410

layout(location = 0) in vec3 a_v;
layout(location = 1) in vec3 a_uv;
layout(location = 2) in vec3 a_n;

out vec3 pos;
out vec2 v_uv;
out float texture_id;
out vec3 n;
out vec3 l;
out vec4 viewSpace;

uniform mat4 P;
uniform mat4 V;
uniform mat4 M;
uniform vec3 light_worldspace;

void main() {
	pos = (M * vec4(a_v.xyz, 1.0)).xyz;
    gl_Position = P * V * M * vec4(a_v.xyz, 1.0);
    v_uv = vec2(a_uv.xy);
    texture_id = a_uv.z;
    
    vec3 vertexPosition_cameraspace = ( V * M * vec4(a_v.xyz,1)).xyz;
	vec3 EyeDirection_cameraspace = vec3(0,0,0) - vertexPosition_cameraspace;
	
	vec3 LightPosition_cameraspace = ( V * vec4(light_worldspace.xyz,1)).xyz;
	vec3 LightDirection_cameraspace = LightPosition_cameraspace + EyeDirection_cameraspace;
	
	vec3 normal_modelspace = ( V * M * vec4(a_n.xyz,0)).xyz;
	
	l = LightDirection_cameraspace;
	n = normal_modelspace;
	
	viewSpace = V * M * vec4(a_v.xyz,1);
}