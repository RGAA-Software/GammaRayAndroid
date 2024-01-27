#version 320 es
#extension GL_OES_EGL_image_external_essl3 : require

precision mediump float;
in vec2 v_texPosition;
uniform samplerExternalOES sTexture;

out vec4 FragColor;

void main() {
    FragColor = texture(sTexture, v_texPosition);
//    FragColor = vec4(1.0, 0.0, 1.0, 1.0);
}