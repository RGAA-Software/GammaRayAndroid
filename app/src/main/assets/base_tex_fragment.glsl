#version 320 es

precision mediump float;

in vec2 outTex;

uniform sampler2D image;

out vec4 FragColor;

void main() {
    FragColor = texture(image, outTex);
    //FragColor = vec4(1.0, 1.0, 1.0, 1.0);
}
