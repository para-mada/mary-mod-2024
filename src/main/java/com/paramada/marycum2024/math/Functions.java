package com.paramada.marycum2024.math;

import static net.minecraft.util.math.MathHelper.clamp;

public class Functions {
    public static double toRadians(double angle) {
        return angle * Math.PI / 180;
    }

    public static double toDegrees(double radians) {
        return 180 * radians / Math.PI;
    }

    public static float normalizeDegree(final float degree) {
        float newDeg = degree % 360;
        if (newDeg < 0) {
            return newDeg + 360;
        }
        return newDeg;
    }

    /**
     * @param originAngle in degrees
     * @param targetAngle in degrees
     * @param timeDelta   sub frame
     * @return lerped angle
     */
    public static float lerpAngleYaw(final float originAngle, final float targetAngle, final float timeDelta) {
        float normOrigin = normalizeDegree(originAngle);
        float normTarget = normalizeDegree(targetAngle);

        float deltaDegree = normTarget - normOrigin;

        // 3) Ajusta para usar la ruta más corta (-180, 180]
        if (deltaDegree > 180f) {
            deltaDegree -= 360f;
        } else if (deltaDegree <= -180f) {
            deltaDegree += 360f;
        }

        final float td = clamp(timeDelta, 0f, 1f);
        final float lerped = normOrigin + deltaDegree * td;

        return normalizeDegree(lerped);
    }

    /**
     * Suavizado exponencial (tipo "smooth lerp") con deltaTime.
     * Útil para que el comportamiento sea consistente a distintos FPS.
     *
     * @param current   yaw actual (grados)
     * @param target    yaw objetivo (grados)
     * @param smooth    agresividad del suavizado por segundo (ej. 8–12). Mayor = más rápido
     * @param deltaTime segundos transcurridos desde el frame anterior
     * @return nuevo yaw en [0, 360)
     */
    public static float stepAngleYaw(float current, float target, float smooth, float deltaTime) {
        // Convertimos el "smooth" por segundo a un t efectivo para este frame:
        // t = 1 - e^(-smooth * dt)  → suavizado exponencial estable por FPS
        float t = 1f - (float) Math.exp(-smooth * Math.max(0f, deltaTime));
        return lerpAngleYaw(current, target, t);
    }

    /**
     * Igual que stepAngleYaw, pero además limita la velocidad angular por segundo.
     *
     * @param current         yaw actual (grados)
     * @param target          yaw objetivo (grados)
     * @param smooth          agresividad del suavizado (por segundo)
     * @param deltaTime       segundos desde el frame anterior
     * @param maxDegPerSecond velocidad angular máxima permitida (grados/seg). Ej. 360
     * @return nuevo yaw en [0, 360)
     */
    public static float stepAngleYawClamped(
            float current, float target, float smooth, float deltaTime, float maxDegPerSecond) {

        current = normalizeDegree(current);
        target  = normalizeDegree(target);

        // Delta corto (-180, 180]
        float delta = target - current;
        if (delta > 180f)      delta -= 360f;
        else if (delta <= -180f) delta += 360f;

        // Paso deseado con suavizado exponencial
        float t = 1f - (float) Math.exp(-smooth * Math.max(0f, deltaTime));
        float desiredStep = delta * clamp(t, 1f, 1f);

        // Limitar magnitud del paso por frame
        float maxStep = Math.max(0f, maxDegPerSecond) * Math.max(0f, deltaTime);
        if (desiredStep >  maxStep) desiredStep =  maxStep;
        if (desiredStep < -maxStep) desiredStep = -maxStep;

        return normalizeDegree(current + desiredStep);
    }
}
