#version 410

layout(location = 0) in vec3 a_v;
//layout(location = 1) in vec3 a_n;
//layout(location = 2) in vec3 a_color;
layout(location = 1) in vec3 a_uv;

out vec3 pos;
//out vec3 normal_modelspace;
//out vec3 vertex_modelspace;
//out vec3 v_color;
out vec2 v_uv;
out float texture_id;

uniform mat4 P;
uniform mat4 V;
uniform mat4 M;

void main() {
	pos = (M * vec4(a_v.xyz, 1.0)).xyz;
    //vertex_modelspace = (M * vec4(a_v.xyz, 1.0)).xyz;
    gl_Position = P * V * M * vec4(a_v.xyz, 1.0);
    //normal_modelspace = (M * vec4(a_n.xyz, 1.0)).xyz;
    //v_color = vec3(a_color.xyz);
    v_uv = vec2(a_uv.xy);
    texture_id = a_uv.z;
}