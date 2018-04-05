#version 410

in vec2 v_uv;
in float texture_id;
in vec3 pos;
in vec3 n;
in vec3 l;
in vec4 viewSpace;

out vec4 color;

uniform sampler2D u_texture_diffuse[16];

const vec3 fogColor = vec3(1.0, 1.0, 1.0);
const float FogDistFactor = 0.012;
const float FogDensity = 0.01;

const vec3 normalColor = vec3(0.1, 0.1, 0.1);
const float normalDensity = 0.4;

void main() {
	float fogDist = 0;
	float fogFactor = 0;
	 
	fogDist = length(viewSpace);
	fogDist = exp(fogDist * FogDistFactor);
	fogFactor = 1.0 / exp(fogDist * FogDensity);
    fogFactor = clamp(fogFactor, 0, 1.0);
    
    float normalDist = 0;
	float normalFactor = 0;
	
	vec3 n2 = normalize(n);
	vec3 l2 = normalize(vec3(pos.xyz));
	normalDist = exp(dot(n2, l2) / 3);
	normalFactor = 1.0 / exp(normalDist * normalDensity);
	normalFactor = clamp(normalFactor, 0, 1.0);
	
	float ambient = 0.95;
	if (int(texture_id) < 0)
	{
		color = vec4(mix(vec3(0.02, 0.19, 0.71), vec3(0.02, 0.58, 0.69), fogFactor), 0.9);
	}
	else if (pos.y < 62)
	{
		color = vec4(mix(vec3(0.02, 0.19, 0.71), mix(normalColor, texture(u_texture_diffuse[int(texture_id)], v_uv.xy).rgb, normalFactor), fogFactor * (pos.y / 62)), 1);
	}
	else
	{	
		color = vec4(mix(normalColor, mix(fogColor, texture(u_texture_diffuse[int(texture_id)], v_uv.xy).rgb, fogFactor), normalFactor), 1);
	}
}