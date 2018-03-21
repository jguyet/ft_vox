#version 410

in vec3 normal_modelspace;
in vec3 vertex_modelspace;
in vec3 v_color;

out vec3 color;
uniform vec3 light_worldspace;

void main() {
  vec3 n = normalize(normal_modelspace);
  vec3 l = normalize(light_worldspace - vertex_modelspace);
  float cosTheta = clamp( dot( n, l), 0,1 );
  float ambient = 0.05;

  color = vec3(v_color.xyz) * (cosTheta + ambient);
}