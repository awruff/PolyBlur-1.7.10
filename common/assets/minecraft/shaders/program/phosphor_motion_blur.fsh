#version 120

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;

varying vec2 texCoord;

uniform vec2 InSize;

uniform float BlendFactor = 0.7;
uniform float Mode = 1.0;

void main() {
    vec4 curr = texture2D(DiffuseSampler, texCoord);
    vec4 prev = texture2D(PrevSampler, texCoord);

    float feedback = clamp(BlendFactor, 0.0, 0.99);

    vec3 blended;
    if (Mode < 0.5) {
        blended = max(prev.rgb * feedback, curr.rgb);
    } else if (Mode < 1.5) {
        blended = mix(curr.rgb, prev.rgb, feedback);
    } else {
        float a = min(1.0 - 0.325, feedback * 0.95);
        blended = prev.rgb * a + curr.rgb * (1.0 - a);
    }

    gl_FragColor = vec4(blended, 1.0);
}
