#version 410

layout(location = 0) in vec3 a_v;
layout(location = 1) in vec3 a_n;
layout(location = 2) in vec3 a_color;

out vec3 normal_modelspace;
out vec3 vertex_modelspace;
out vec3 v_color;

uniform mat4 P;
uniform mat4 V;
uniform mat4 M;

void main() {
    vertex_modelspace = (M * vec4(a_v.xyz, 1.0)).xyz;
    gl_Position = P * V * M * vec4(a_v.xyz, 1.0);
    normal_modelspace = (M * vec4(a_n.xyz, 1.0)).xyz;
    v_color = vec3(a_color.xyz);
//
//  	// Vector that goes from the vertex to the camera, in camera space.
//  	// In camera space, the camera is at the origin (0,0,0).
//  	vec3 vertexPosition_cameraspace = (V * M * vec4(vertexPos,1)).xyz;
//  	vec3 EyeDirection_cameraspace = vec3(0,0,0) - vertexPosition_cameraspace;
//
//  	// Vector that goes from the vertex to the light, in camera space. M is ommited because it's identity.
//  	vec3 LightPosition_cameraspace = ( V * vec4(light,1)).xyz;
//  	LightDirection_cameraspace = LightPosition_cameraspace + EyeDirection_cameraspace;
}