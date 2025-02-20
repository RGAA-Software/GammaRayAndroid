#version 320 es

precision mediump float;

in vec3 outColor;

out vec4 FragColor;

void main() {
    FragColor = vec4(outColor, 1.0);
    FragColor = vec4(1.0, 1.0, 0.1, 1.0);
}