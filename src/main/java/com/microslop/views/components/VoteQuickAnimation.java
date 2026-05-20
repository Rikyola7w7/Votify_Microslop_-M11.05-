package com.microslop.views.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;

/**
 * Quick but epic vote confirmation for intermediate votes.
 * A fast flash, glowing checkmark, brief particle burst, and swift dissolve.
 * Much faster than VoteSuccessAnimation — under 2 seconds total.
 */
public class VoteQuickAnimation extends Div {

    private static final String[] COLORS = {
        "#f59e0b", "#fbbf24", "#10b981", "#34d399", "#6366f1", "#818cf8", "#ec4899"
    };

    public VoteQuickAnimation(Runnable onComplete) {
        injectStyles();
        buildDOM();
        scheduleSequence(onComplete);
    }

    private void injectStyles() {
        getElement().executeJs(
            "var s=document.createElement('style');s.textContent=`" +
            "@keyframes qkBgIn{0%{opacity:0}100%{opacity:1}}" +
            "@keyframes qkBgOut{0%{opacity:1;filter:brightness(1)}100%{opacity:0;filter:brightness(2) blur(15px)}}" +
            "@keyframes qkFlash{0%{opacity:0}15%{opacity:.85}100%{opacity:0}}" +
            "@keyframes qkCheckIn{0%{transform:translate(-50%,-50%) scale(0) rotate(-45deg);opacity:0}50%{transform:translate(-50%,-50%) scale(1.2) rotate(0);opacity:1}100%{transform:translate(-50%,-50%) scale(1) rotate(0);opacity:1}}" +
            "@keyframes qkCheckGlow{0%{box-shadow:0 0 20px rgba(16,185,129,.6),0 0 40px rgba(16,185,129,.3)}100%{box-shadow:0 0 40px rgba(16,185,129,.9),0 0 80px rgba(16,185,129,.5),0 0 120px rgba(16,185,129,.2)}}" +
            "@keyframes qkRing{0%{width:10px;height:10px;opacity:.7;border-width:2px}100%{width:min(50vw,400px);height:min(50vw,400px);opacity:0;border-width:1px}}" +
            "@keyframes qkTextIn{0%{opacity:0;transform:translateY(15px) scale(.9)}100%{opacity:1;transform:translateY(0) scale(1)}}" +
            "@keyframes qkScanIn{0%{opacity:0}100%{opacity:.25}}" +
            "`;document.head.appendChild(s);"
        );
    }

    private void buildDOM() {
        getElement().executeJs(
            "var ov=document.createElement('div');ov.id='qk-overlay';" +
            "ov.style.cssText='position:fixed;inset:0;z-index:99999;pointer-events:none;overflow:hidden;';" +
            "document.body.appendChild(ov);" +
            "\n" +
            // Backdrop
            "var bg=document.createElement('div');" +
            "bg.style.cssText='position:absolute;inset:0;background:radial-gradient(ellipse at center,rgba(15,23,42,.96),rgba(2,6,23,.98));opacity:0;animation:qkBgIn .25s ease forwards;';" +
            "ov.appendChild(bg);" +
            "\n" +
            // Scanlines
            "var scan=document.createElement('div');" +
            "scan.style.cssText='position:absolute;inset:0;background:repeating-linear-gradient(0deg,transparent,transparent 2px,rgba(0,0,0,.03) 2px,rgba(0,0,0,.03) 4px);opacity:0;animation:qkScanIn .2s ease forwards .15s;';" +
            "ov.appendChild(scan);" +
            "\n" +
            // Shockwave ring
            "var ring=document.createElement('div');" +
            "ring.style.cssText='position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);border-radius:50%;border:2px solid rgba(16,185,129,.5);width:10px;height:10px;opacity:0;animation:qkRing .8s cubic-bezier(.22,1,.36,1) forwards .3s;';" +
            "ov.appendChild(ring);" +
            "\n" +
            // Flash
            "var flash=document.createElement('div');" +
            "flash.style.cssText='position:absolute;inset:0;background:rgba(16,185,129,.6);opacity:0;animation:qkFlash .4s ease forwards .25s;';" +
            "ov.appendChild(flash);" +
            "\n" +
            // Checkmark circle
            "var check=document.createElement('div');" +
            "check.style.cssText='position:absolute;left:50%;top:45%;width:90px;height:90px;border-radius:50%;background:linear-gradient(135deg,#10b981,#059669);transform:translate(-50%,-50%) scale(0);opacity:0;animation:qkCheckIn .5s cubic-bezier(.16,1,.3,1) forwards .35s,qkCheckGlow 1.5s ease-in-out infinite alternate .85s;';" +
            "\n" +
            // SVG checkmark
            "var svg=document.createElementNS('http://www.w3.org/2000/svg','svg');svg.setAttribute('viewBox','0 0 24 24');svg.setAttribute('width','48');svg.setAttribute('height','48');svg.style.cssText='position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);';" +
            "var path=document.createElementNS('http://www.w3.org/2000/svg','path');path.setAttribute('d','M5 13l4 4L19 7');path.setAttribute('stroke','white');path.setAttribute('stroke-width','3');path.setAttribute('fill','none');path.setAttribute('stroke-linecap','round');path.setAttribute('stroke-linejoin','round');" +
            "svg.appendChild(path);check.appendChild(svg);ov.appendChild(check);" +
            "\n" +
            // Text
            "var tw=document.createElement('div');" +
            "tw.style.cssText='position:absolute;left:50%;top:60%;transform:translate(-50%,0);text-align:center;opacity:0;animation:qkTextIn .4s ease forwards .6s;';" +
            "var txt=document.createElement('div');txt.textContent='VOTE RECORDED';" +
            "txt.style.cssText='font-family:Courier New,monospace;font-size:clamp(1.2rem,4vw,2rem);font-weight:800;letter-spacing:.15em;color:#fef3c7;text-shadow:0 0 30px rgba(251,191,36,.7),0 0 60px rgba(251,191,36,.3);white-space:nowrap;';" +
            "tw.appendChild(txt);ov.appendChild(tw);" +
            "\n" +
            // Particle canvas
            "var c=document.createElement('canvas');c.id='qk-canvas';" +
            "c.style.cssText='position:absolute;inset:0;width:100%;height:100%;pointer-events:none;';" +
            "ov.appendChild(c);" +
            "\n" +
            "qkParticles();"
        );
    }

    private void scheduleSequence(Runnable onComplete) {
        // Quick particle burst
        getElement().executeJs(
            "function qkParticles(){" +
            "var c=document.getElementById('qk-canvas');if(!c)return;" +
            "var ctx=c.getContext('2d');var W=c.width=innerWidth;var H=c.height=innerHeight;" +
            "var cx=W/2,cy=H*.45;var pts=[];var cols=" + arrayToJs(COLORS) + ";" +
            "setTimeout(function(){" +
            "  for(var i=0;i<60;i++){" +
            "    var a=Math.PI*2*i/60+(Math.random()-.5)*.5;" +
            "    var sp=3+Math.random()*5;" +
            "    pts.push({x:cx,y:cy,vx:Math.cos(a)*sp,vy:Math.sin(a)*sp," +
            "      life:35+Math.random()*15,age:0,size:2+Math.random()*3," +
            "      color:cols[Math.floor(Math.random()*cols.length)],fric:.96});" +
            "  }" +
            "},350);" +
            "var run=true;function frame(){" +
            "  if(!run)return;ctx.clearRect(0,0,W,H);" +
            "  for(var i=pts.length-1;i>=0;i--){" +
            "    var p=pts[i];p.age++;if(p.age>p.life){pts.splice(i,1);continue;}" +
            "    p.vx*=p.fric;p.vy*=p.fric;p.x+=p.vx;p.y+=p.vy;" +
            "    var al=1-(p.age/p.life);ctx.globalAlpha=al;ctx.fillStyle=p.color;" +
            "    ctx.beginPath();ctx.arc(p.x,p.y,p.size*al,0,Math.PI*2);ctx.fill();" +
            "  }" +
            "  ctx.globalAlpha=1;" +
            "  if(pts.length>0)requestAnimationFrame(frame);else run=false;" +
            "}" +
            "setTimeout(frame,40);" +
            "}"
        );

        // Dissolve and callback
        getElement().executeJs(
            "setTimeout(function(){" +
            "  var ov=document.getElementById('qk-overlay');" +
            "  if(ov){ov.style.animation='qkBgOut .6s ease forwards';setTimeout(function(){ov.remove();},800);}" +
            "},1600);"
        );

        UI ui = UI.getCurrent();
        if (ui != null) {
            ui.accessLater(v -> {
                try { Thread.sleep(2200); } catch (InterruptedException ignored) {}
                onComplete.run();
            }, () -> {});
        }
    }

    private static String arrayToJs(String[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append("'").append(arr[i]).append("'");
            if (i < arr.length - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }
}
