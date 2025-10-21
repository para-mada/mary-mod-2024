package com.paramada.marycum2024.util.souls;

import net.minecraft.util.math.Vec3d;

public class SoulsCamManager {
    public static float normDeg(float a) {
        a %= 360f;
        if (a < 0f) a += 360f;
        return a;
    }

    /** Diferencia angular corta (target - current) en (-180,180]. */
    public static float shortDeltaDeg(float current, float target) {
        float delta = normDeg(target) - normDeg(current);
        if (delta > 180f) delta -= 360f;
        else if (delta <= -180f) delta += 360f;
        return delta;
    }

    /**
     * SmoothDamp de ángulo (grados) con límite de velocidad.
     * currentVelocity es un valor mutable (pásalo por referencia; aquí se devuelve).
     */
    public static float smoothDampAngle(
            float current, float target,
            float[] currentVelocity /* len=1 */,
            float smoothTime, float maxDegPerSecond, float deltaTime) {

        // Seguridad
        smoothTime = Math.max(0.0001f, smoothTime);
        deltaTime  = Math.max(0f, deltaTime);

        // Paso máximo por frame según límite de velocidad
        float maxDelta = Math.max(0f, maxDegPerSecond) * deltaTime;

        // Error angular corto
        float delta = shortDeltaDeg(current, target);

        // Limitar cuánto podemos acercarnos en este frame por velocidad máxima
        float targetTemp = current + clamp(delta, -maxDelta, maxDelta);

        // Constantes para amortiguación crítica (discreto)
        float omega = 2f / smoothTime;
        float x = omega * deltaTime;
        // Aproximación e^(-omega*dt)
        float exp = 1f / (1f + x + 0.48f*x*x + 0.235f*x*x*x);

        float change = shortDeltaDeg(current, targetTemp);
        float temp = (currentVelocity[0] + omega * change) * deltaTime;
        float newVel = (currentVelocity[0] - omega * temp) * exp;
        float output = current + (change + temp) * exp;

        // Corrige si nos pasamos por el límite de velocidad
        float origToTarget = shortDeltaDeg(targetTemp, target);
        float newToTarget  = shortDeltaDeg(output,   target);
        if (origToTarget * newToTarget < 0f) { // cruzamos el objetivo
            output = target;
            newVel = 0f;
        }

        currentVelocity[0] = newVel;
        return normDeg(output);
    }

    private static float clamp(float v, float lo, float hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    /**
     * Calcula el yaw objetivo con "lead": predice posición futura del objetivo y del pivote
     * usando sus velocidades. Luego aplica SmoothDampAngle.
     *
     * @param yawActual     yaw actual de cámara (grados)
     * @param yawVel        velocidad angular actual (grados/seg) como float[1] (mutable)
     * @param pivotPos      posición del jugador/pivote (actual)
     * @param pivotVel      velocidad del jugador/pivote (m/s o bloques/s)
     * @param targetPos     posición del objetivo (actual)
     * @param targetVel     velocidad del objetivo (m/s o bloques/s)
     * @param deltaTime     segundos desde el frame anterior
     * @param smoothTime    tiempo de asentamiento ~0.08–0.18s (más bajo = más rápido)
     * @param maxDegPerSec  límite de velocidad angular (p.ej. 360–540)
     * @param baseLead      lead base en segundos (p.ej. 0.06–0.12)
     * @return nuevo yaw en [0,360)
     */
    public static float updateYawWithPrediction(
            float yawActual, float[] yawVel,
            Vec3d pivotPos, Vec3d pivotVel,
            Vec3d targetPos, Vec3d targetVel,
            float deltaTime, float smoothTime, float maxDegPerSec, float baseLead) {

        // 1) Tiempo de predicción dinámico según velocidad relativa y distancia
        Vec3d relVel = targetVel.subtract(pivotVel);
        float relSpeed = (float) relVel.length();       // bloques/s
        float dist = (float) targetPos.subtract(pivotPos).length();

        // Heurística: más cerca → menos lead; más velocidad relativa → más lead
        float lead = baseLead
                + clamp(relSpeed / 20f, 0f, 0.15f)   // hasta +150 ms por velocidad
                + clamp(dist / 60f, 0f, 0.10f);      // hasta +100 ms por distancia
        lead = clamp(lead, 0f, 0.30f); // 0–300 ms total

        // 2) Posiciones futuras estimadas
        Vec3d futTarget = targetPos.add(targetVel.multiply(lead));
        Vec3d futPivot  = pivotPos.add(pivotVel.multiply(lead));

        // 3) Yaw objetivo desde el pivote hacia el objetivo futuro
        Vec3d dir = futTarget.subtract(futPivot);
        float targetYaw = (float) Math.toDegrees(Math.atan2(dir.z, dir.x)); // XZ plano
        targetYaw = normDeg(targetYaw);

        // 4) Suavizado con velocidad angular
        return smoothDampAngle(yawActual, targetYaw, yawVel, smoothTime, maxDegPerSec, deltaTime);
    }
}
