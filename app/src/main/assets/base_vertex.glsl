#version 320 es

layout(location=0) in vec3 aPos;
layout(location=1) in vec3 aColor;
layout(location=2) in vec2 aTex;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

uniform float pointSize;

out vec3 outColor;
out vec2 outTex;
out float outAlpha;

void main() {
    outColor = aColor;
    outTex = aTex;
    gl_Position = projection * view * model * vec4(aPos, 1);
    gl_Position = vec4(gl_Position.x, gl_Position.y * -1.0, gl_Position.z, gl_Position.w);
    gl_PointSize = pointSize;
}