#ifdef GL_ES
precision mediump float;
#endif
varying vec2 v_texCoords;
uniform sampler2D u_texture;
void main()
{
    vec2 center = vec2(0.5, 0.5);
    float radius = 0.5;
    float dist = distance(v_texCoords, center);
    float alpha = step(dist, radius);
    vec4 texColor = texture2D(u_texture, v_texCoords);
    gl_FragColor = vec4(texColor.rgb, texColor.a * alpha);
}