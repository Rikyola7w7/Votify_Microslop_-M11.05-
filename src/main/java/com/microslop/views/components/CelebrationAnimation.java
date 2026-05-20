package com.microslop.views.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;

/**
 * Maximalist celebration animation for important actions:
 * registration, login, competition creation, voter registration.
 *
 * A golden orb ignites at center, radiating concentric shockwaves,
 * geometric confetti erupts, a bold message appears with chromatic split,
 * then the whole scene dissolves with trailing embers.
 */
public class CelebrationAnimation extends Div {

    private static final String[] GOLD_PALETTE = {
        "#f59e0b", "#fbbf24", "#fcd34d", "#fef3c7",
        "#d97706", "#b45309", "#92400e",
        "#10b981", "#34d399", "#6ee7b7",
        "#6366f1", "#818cf8",
        "#ec4899", "#f472b6"
    };

    private final String message;
    private final String subtitle;
    private Runnable pendingCallback;

    public CelebrationAnimation(String message, String subtitle, Runnable onComplete) {
        this.message = message;
        this.subtitle = subtitle;
        this.pendingCallback = onComplete;
        injectStyles();
        buildDOM();
    }

    @Override
    protected void onAttach(com.vaadin.flow.component.AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (pendingCallback != null) {
            scheduleSequence(pendingCallback);
            pendingCallback = null;
        }
    }

    private void injectStyles() {
        getElement().executeJs(
            "var s=document.createElement('style');s.textContent=`" +
            // Overlay backdrop
            "@keyframes celBgIn{0%{opacity:0}100%{opacity:1}}" +
            "@keyframes celDissolve{0%{opacity:1;filter:brightness(1)}100%{opacity:0;filter:brightness(2.5) blur(30px)}}" +
            // Flash
            "@keyframes celFlash{0%{opacity:0}12%{opacity:.95}100%{opacity:0}}" +
            // Shockwave rings
            "@keyframes celRing{0%{width:10px;height:10px;opacity:.8;border-width:2px}100%{width:min(80vw,600px);height:min(80vw,600px);opacity:0;border-width:1px}}" +
            // Orb
            "@keyframes celOrbIn{0%{width:0;height:0;opacity:0}35%{width:70px;height:70px;opacity:1}60%{width:90px;height:90px;opacity:.9}100%{width:0;height:0;opacity:0}}" +
            "@keyframes celOrbPulse{0%{transform:scale(1);opacity:.5}100%{transform:scale(2);opacity:0}}" +
            // Text
            "@keyframes celTextIn{0%{opacity:0;transform:scaleY(0);filter:blur(8px)}60%{opacity:1;transform:scaleY(1.05);filter:blur(0)}100%{opacity:1;transform:scaleY(1);filter:blur(0)}}" +
            "@keyframes celTextGlow{0%{text-shadow:0 0 30px rgba(251,191,36,.7),0 0 60px rgba(251,191,36,.3)}100%{text-shadow:0 0 50px rgba(251,191,36,1),0 0 100px rgba(251,191,36,.5),0 0 180px rgba(251,191,36,.2)}}" +
            "@keyframes celSubIn{0%{opacity:0;transform:translateY(12px)}100%{opacity:1;transform:translateY(0)}}" +
            // Chromatic split
            "@keyframes celCR{0%{transform:translate(12px,-8px);opacity:.8}100%{transform:translate(0,0);opacity:0;filter:blur(2px)}}" +
            "@keyframes celCG{0%{transform:translate(-12px,8px);opacity:.8}100%{transform:translate(0,0);opacity:0;filter:blur(2px)}}" +
            "@keyframes celCB{0%{transform:translate(8px,12px);opacity:.8}100%{transform:translate(0,0);opacity:0;filter:blur(2px)}}" +
            // Particle canvas
            "@keyframes celScanIn{0%{opacity:0}100%{opacity:.3}}" +
            "`;document.head.appendChild(s);"
        );
    }

    private void buildDOM() {
        String escapedMsg = message.replace("'", "\\'").replace("\"", "\\\"");
        String escapedSub = subtitle.replace("'", "\\'").replace("\"", "\\\"");

        getElement().executeJs(
            "var ov=document.createElement('div');ov.id='cel-overlay';" +
            "ov.style.cssText='position:fixed;inset:0;z-index:99999;pointer-events:none;overflow:hidden;';" +
            "document.body.appendChild(ov);" +
            "\n" +
            // Backdrop
            "var bg=document.createElement('div');" +
            "bg.style.cssText='position:absolute;inset:0;background:radial-gradient(ellipse at center,rgba(15,23,42,.97),rgba(2,6,23,.99));opacity:0;animation:celBgIn .4s ease forwards .1s;';" +
            "ov.appendChild(bg);" +
            "\n" +
            // Scanlines
            "var scan=document.createElement('div');" +
            "scan.style.cssText='position:absolute;inset:0;background:repeating-linear-gradient(0deg,transparent,transparent 2px,rgba(0,0,0,.04) 2px,rgba(0,0,0,.04) 4px);opacity:0;animation:celScanIn .3s ease forwards .5s;pointer-events:none;';" +
            "ov.appendChild(scan);" +
            "\n" +
            // Shockwave rings
            "for(var r=0;r<3;r++){" +
            "  var ring=document.createElement('div');" +
            "  var delays=['.8s','1s','1.2s'];" +
            "  var colors=['rgba(251,191,36,.5)','rgba(16,185,129,.4)','rgba(99,102,241,.3)'];" +
            "  ring.style.cssText='position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);border-radius:50%;border:2px solid '+colors[r]+';width:10px;height:10px;opacity:0;animation:celRing 1.4s cubic-bezier(.22,1,.36,1) forwards '+delays[r]+';';" +
            "  ov.appendChild(ring);" +
            "}" +
            "\n" +
            // Central orb
            "var orb=document.createElement('div');" +
            "orb.style.cssText='position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);width:0;height:0;border-radius:50%;opacity:0;animation:celOrbIn .9s cubic-bezier(.16,1,.3,1) forwards .5s;';" +
            "var orbInner=document.createElement('div');" +
            "orbInner.style.cssText='position:absolute;inset:0;border-radius:50%;background:radial-gradient(circle,#fef3c7,#fbbf24,#f59e0b);';" +
            "var orbGlow=document.createElement('div');" +
            "orbGlow.style.cssText='position:absolute;inset:-25px;border-radius:50%;background:radial-gradient(circle,rgba(251,191,36,.7),rgba(245,158,11,.3),transparent 70%);animation:celOrbPulse 1.2s ease-in-out infinite alternate .5s;';" +
            "orb.appendChild(orbInner);orb.appendChild(orbGlow);ov.appendChild(orb);" +
            "\n" +
            // Flash
            "var flash=document.createElement('div');" +
            "flash.style.cssText='position:absolute;inset:0;background:white;opacity:0;animation:celFlash .5s ease forwards .8s;';" +
            "ov.appendChild(flash);" +
            "\n" +
            // Text container
            "var tw=document.createElement('div');" +
            "tw.style.cssText='position:absolute;left:0;top:50%;width:100%;transform:translateY(-50%);text-align:center;opacity:0;animation:celTextIn .01s forwards 1.3s;';" +
            "\n" +
            // Chromatic layers
            "var msg='" + escapedMsg + "';" +
            "var sub='" + escapedSub + "';" +
            "\n" +
            // Text stack: relative container for chromatic layers, subtitle outside
            "var textStack=document.createElement('div');" +
            "textStack.style.cssText='position:relative;display:inline-block;';" +
            "\n" +
            "function mkText(cls,anim){" +
            "  var d=document.createElement('div');d.textContent=msg;" +
            "  d.style.cssText='font-family:Courier New,monospace;font-size:clamp(1.8rem,7vw,4rem);font-weight:900;letter-spacing:.2em;white-space:nowrap;position:absolute;left:0;top:0;width:100%;'+anim;" +
            "  if(cls){" +
            "    var cols={r:'rgba(244,63,94,.6)',g:'rgba(16,185,129,.6)',b:'rgba(99,102,241,.6)'};" +
            "    d.style.color=cols[cls]||'white';" +
            "  } else {" +
            "    d.style.color='#fef3c7';d.style.zIndex='4';" +
            "    d.style.textShadow='0 0 40px rgba(251,191,36,.8),0 0 80px rgba(251,191,36,.4),0 0 120px rgba(251,191,36,.2)';" +
            "    d.style.animation='celTextIn .7s cubic-bezier(.16,1,.3,1) forwards 1.3s,celTextGlow 2s ease-in-out infinite alternate 2s;';" +
            "  }" +
            "  return d;" +
            "}" +
            // Invisible spacer so the relative container has the right height
            "var spacer=document.createElement('div');spacer.textContent=msg;" +
            "spacer.style.cssText='font-family:Courier New,monospace;font-size:clamp(1.8rem,7vw,4rem);font-weight:900;letter-spacing:.2em;white-space:nowrap;visibility:hidden;';" +
            "textStack.appendChild(spacer);" +
            "textStack.appendChild(mkText('r','animation:celCR 1.2s ease forwards 1.3s;z-index:1;'));" +
            "textStack.appendChild(mkText('g','animation:celCG 1.2s ease forwards 1.3s;z-index:2;'));" +
            "textStack.appendChild(mkText('b','animation:celCB 1.2s ease forwards 1.3s;z-index:3;'));" +
            "textStack.appendChild(mkText(null,''));" +
            "tw.appendChild(textStack);" +
            "\n" +
            // Subtitle
            "var subEl=document.createElement('div');" +
            "subEl.textContent=sub;" +
            "subEl.style.cssText='font-family:Courier New,monospace;font-size:clamp(.7rem,2vw,1rem);color:rgba(148,163,184,.9);letter-spacing:.1em;margin-top:1.2rem;opacity:0;animation:celSubIn .7s ease forwards 1.8s;position:relative;clear:both;';" +
            "tw.appendChild(subEl);" +
            "ov.appendChild(tw);" +
            "\n" +
            // Particle canvas
            "var canvas=document.createElement('canvas');" +
            "canvas.id='cel-canvas';" +
            "canvas.style.cssText='position:absolute;inset:0;width:100%;height:100%;pointer-events:none;';" +
            "ov.appendChild(canvas);" +
            "\n" +
            // Init particles
            "celInitParticles();"
        );
    }

    private void scheduleSequence(Runnable onComplete) {
        // Particle system
        getElement().executeJs(
            "function celInitParticles(){" +
            "var c=document.getElementById('cel-canvas');if(!c)return;" +
            "var ctx=c.getContext('2d');var W=c.width=innerWidth;var H=c.height=innerHeight;" +
            "var cx=W/2,cy=H/2;var pts=[];" +
            "var cols=" + arrayToJs(GOLD_PALETTE) + ";" +
            "function spawn(n,o){" +
            "  for(var i=0;i<n;i++){" +
            "    var a=o.a!==undefined?o.a:(Math.PI*2*i/n)+((Math.random()-.5)*.5);" +
            "    var sp=(o.sp||3)+Math.random()*(o.sv||3);" +
            "    pts.push({x:o.x||cx,y:o.y||cy,vx:Math.cos(a)*sp,vy:Math.sin(a)*sp," +
            "      life:o.l||50,age:0,size:o.sz||(2+Math.random()*3)," +
            "      color:cols[Math.floor(Math.random()*cols.length)],trail:o.t||false," +
            "      grav:o.g||0,fric:o.f||.97});" +
            "  }" +
            "}" +
            // Burst at flash
            "setTimeout(function(){spawn(90,{sp:5,sv:6,sz:2.5,l:65,t:true,f:.96});spawn(30,{sp:1.5,sv:2,sz:5,l:45});},850);" +
            // Ring sparks
            "setTimeout(function(){for(var i=0;i<20;i++){var a=Math.PI*2*i/20;spawn(2,{x:cx+Math.cos(a)*40,y:cy+Math.sin(a)*40,a:a,sp:2.5,sv:1.5,sz:1.5,l:40});}},1100);" +
            // Rising embers
            "setTimeout(function(){spawn(40,{sp:.8,sv:1.5,sz:1.5,l:70,g:-.025,f:.99});},1600);" +
            "var run=true;function frame(){" +
            "  if(!run)return;ctx.clearRect(0,0,W,H);" +
            "  for(var i=pts.length-1;i>=0;i--){" +
            "    var p=pts[i];p.age++;if(p.age>p.life){pts.splice(i,1);continue;}" +
            "    p.vx*=p.fric;p.vy*=p.fric;p.vy+=p.grav;p.x+=p.vx;p.y+=p.vy;" +
            "    var al=1-(p.age/p.life);ctx.globalAlpha=al;ctx.fillStyle=p.color;" +
            "    ctx.shadowBlur=p.trail?12:0;ctx.shadowColor=p.color;" +
            "    ctx.beginPath();ctx.arc(p.x,p.y,p.size*al,0,Math.PI*2);ctx.fill();" +
            "    if(p.trail){ctx.globalAlpha=al*.25;ctx.beginPath();ctx.moveTo(p.x,p.y);" +
            "      ctx.lineTo(p.x-p.vx*2.5,p.y-p.vy*2.5);ctx.strokeStyle=p.color;ctx.lineWidth=p.size*.4;ctx.stroke();}" +
            "  }" +
            "  ctx.globalAlpha=1;ctx.shadowBlur=0;" +
            "  if(pts.length>0)requestAnimationFrame(frame);else setTimeout(function(){run=false;},400);" +
            "}" +
            "setTimeout(frame,40);" +
            "}"
        );

        // Dissolve after sequence
        getElement().executeJs(
            "setTimeout(function(){" +
            "  var ov=document.getElementById('cel-overlay');" +
            "  if(ov){ov.style.animation='celDissolve 1s ease forwards';setTimeout(function(){ov.remove();},1200);}" +
            "},3200);"
        );

        UI ui = UI.getCurrent();
        if (ui != null) {
            ui.accessLater(v -> {
                try { Thread.sleep(4000); } catch (InterruptedException ignored) {}
                onComplete.run();
            }, () -> {});
        } else {
            // Fallback: schedule via JS setTimeout if UI context not available
            getElement().executeJs(
                "setTimeout(function(){" +
                "  var ov=document.getElementById('cel-overlay');" +
                "  if(ov) ov.remove();" +
                "},4500);"
            );
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
