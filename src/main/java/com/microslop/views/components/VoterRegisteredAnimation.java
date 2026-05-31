package com.microslop.views.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;

/**
 * Simple voter registration confirmation — just a user icon with a checkmark.
 * Light, fast, appropriate for a common action. No particles, no orbs, no flash.
 */
public class VoterRegisteredAnimation extends Div {

    public VoterRegisteredAnimation(Runnable onComplete) {
        getElement().executeJs(buildAnimationJS());
        scheduleDismiss(onComplete);
    }

    private String buildAnimationJS() {
        return
        // Overlay — lighter
        "var ov=document.createElement('div');ov.id='vr-ov';" +
        "ov.style.cssText='position:fixed;inset:0;z-index:99999;pointer-events:none;display:flex;align-items:center;justify-content:center;flex-direction:column;background:rgba(30,30,47,.75);opacity:0;transition:opacity .25s ease;';" +
        "document.body.appendChild(ov);" +
        "requestAnimationFrame(function(){ov.style.opacity='1';});" +
        "\n" +
        // User icon circle
        "var circle=document.createElement('div');" +
        "circle.style.cssText='width:80px;height:80px;border-radius:50%;background:linear-gradient(135deg,#6C5CE7,#5A4BD1);display:flex;align-items:center;justify-content:center;margin-bottom:20px;transform:scale(0);transition:transform .35s cubic-bezier(.34,1.56,.64,1);box-shadow:0 8px 24px rgba(108,92,231,.3);';" +
        // User SVG icon
        "var svg=document.createElementNS('http://www.w3.org/2000/svg','svg');" +
        "svg.setAttribute('viewBox','0 0 24 24');svg.setAttribute('width','36');svg.setAttribute('height','36');" +
        "svg.style.cssText='opacity:0;transition:opacity .2s ease .15s;';" +
        "var circlePath=document.createElementNS('http://www.w3.org/2000/svg','circle');" +
        "circlePath.setAttribute('cx','12');circlePath.setAttribute('cy','8');circlePath.setAttribute('r','4');" +
        "circlePath.setAttribute('stroke','white');circlePath.setAttribute('stroke-width','2');circlePath.setAttribute('fill','none');" +
        "var bodyPath=document.createElementNS('http://www.w3.org/2000/svg','path');" +
        "bodyPath.setAttribute('d','M4 20c0-4 4-7 8-7s8 3 8 7');" +
        "bodyPath.setAttribute('stroke','white');bodyPath.setAttribute('stroke-width','2');bodyPath.setAttribute('fill','none');" +
        "bodyPath.setAttribute('stroke-linecap','round');" +
        "svg.appendChild(circlePath);svg.appendChild(bodyPath);" +
        // Small checkmark badge
        "var badge=document.createElement('div');" +
        "badge.style.cssText='position:absolute;bottom:-2px;right:-2px;width:28px;height:28px;border-radius:50%;background:#00B894;display:flex;align-items:center;justify-content:center;transform:scale(0);transition:transform .3s cubic-bezier(.34,1.56,.64,1) .2s;box-shadow:0 2px 8px rgba(0,184,148,.4);';" +
        "var bSvg=document.createElementNS('http://www.w3.org/2000/svg','svg');" +
        "bSvg.setAttribute('viewBox','0 0 24 24');bSvg.setAttribute('width','16');bSvg.setAttribute('height','16');" +
        "var bPath=document.createElementNS('http://www.w3.org/2000/svg','path');" +
        "bPath.setAttribute('d','M5 13l4 4L19 7');" +
        "bPath.setAttribute('stroke','white');bPath.setAttribute('stroke-width','3');bPath.setAttribute('fill','none');" +
        "bPath.setAttribute('stroke-linecap','round');bPath.setAttribute('stroke-linejoin','round');" +
        "bSvg.appendChild(bPath);badge.appendChild(bSvg);" +
        "circle.appendChild(svg);circle.appendChild(badge);ov.appendChild(circle);" +
        "\n" +
        // Text
        "var txt=document.createElement('div');" +
        "txt.textContent='VOTER REGISTERED';" +
        "txt.style.cssText='font-family:Courier New,monospace;font-size:clamp(.8rem,2.5vw,1.1rem);font-weight:700;letter-spacing:.1em;color:#A29BFE;text-shadow:0 0 12px rgba(108,92,231,.3);opacity:0;transition:opacity .25s ease .35s;white-space:nowrap;';" +
        "ov.appendChild(txt);" +
        "\n" +
        // Sub
        "var sub=document.createElement('div');" +
        "sub.textContent='Welcome aboard';" +
        "sub.style.cssText='font-family:Courier New,monospace;font-size:clamp(.6rem,1.8vw,.8rem);color:rgba(148,163,184,.7);letter-spacing:.05em;margin-top:6px;opacity:0;transition:opacity .25s ease .45s;white-space:nowrap;';" +
        "ov.appendChild(sub);" +
        "\n" +
        // Animate in
        "setTimeout(function(){circle.style.transform='scale(1)';},150);" +
        "setTimeout(function(){svg.style.opacity='1';},250);" +
        "setTimeout(function(){badge.style.transform='scale(1)';},350);" +
        "setTimeout(function(){txt.style.opacity='1';sub.style.opacity='1';},400);";
    }

    private void scheduleDismiss(Runnable onComplete) {
        getElement().executeJs(
            "setTimeout(function(){" +
            "  var ov=document.getElementById('vr-ov');" +
            "  if(ov){ov.style.opacity='0';setTimeout(function(){ov.remove();},350);}" +
            "},2200);"
        );

        UI ui = UI.getCurrent();
        if (ui != null) {
            java.util.concurrent.CompletableFuture.runAsync(() -> {
                try { Thread.sleep(2600); } catch (InterruptedException ignored) {}
                ui.access(onComplete::run);
            });
        }
    }
}
