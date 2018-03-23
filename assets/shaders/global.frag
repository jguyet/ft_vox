#version 410

//in vec3 normal_modelspace;
//in vec3 vertex_modelspace;
//in vec3 v_color;
in vec2 v_uv;
in float texture_id;
in vec3 pos;

out vec3 color;
//uniform vec3 light_worldspace; 
uniform sampler2D u_texture_diffuse[3];

void main() {
	//vec3 n = normalize(normal_modelspace);
	//vec3 l = normalize(light_worldspace - vertex_modelspace);
	//float cosTheta = clamp( dot( n, vec3(0,0,0)), 0,1 );
	float ambient = 0.60;
	if (pos.y <= 0)
	{
		color = vec3(0.02, 0.54, 0.69);
	}
	else if (pos.y > 80)
	{
		color = vec3(1,1,1);
	}
	else
	{
		color = texture(u_texture_diffuse[int(texture_id)], v_uv.xy).rgb * ambient;//vec3(v_color.xyz);// * (cosTheta + ambient);
	}
}