#version 320 es

layout(location = 0) in vec4 av_Position;
layout(location = 1)in vec2 af_Position;

out vec2 v_texPosition;

void main() {
    v_texPosition = af_Position;
    gl_Position = av_Position;
}