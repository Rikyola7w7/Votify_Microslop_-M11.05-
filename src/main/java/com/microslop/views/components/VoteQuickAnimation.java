package com.microslop.views.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;

/**
 * Quick vote confirmation — refined, matches Votify's design system.
 * Primary purple checkmark, no flashbang, smooth fade.
 */
public class VoteQuickAnimation extends Div {

    private static final String[] COLORS = {
        "#6C5CE7", "#A29BFE", "#00CEC9", "#FD79A8", "#5A4BD1", "#00B894"
    };

    private final int remainingVotes;

    public VoteQuickAnimation(int remainingVotes, Runnable onComplete) {
        this.remainingVotes = remainingVotes;
        injectStyles();
        buildDOM();
        scheduleSequence(onComplete);
    }

    private void injectStyles() {
        getElement().executeJs(
            "var s=document.createElement('style');s.textContent=`" +
            "@keyframes qkBgIn{0%{opacity:0}100%{opacity:1}}" +
            "@keyframes qkBgOut{0%{opacity:1}100%{opacity:0}}" +
            "@keyframes qkRing{0%{width:10px;height:10px;opacity:.5;border-width:2px}100%{width:min(40vw,350px);height:min(40vw,350px);opacity:0;border-width:1px}}" +
            "@keyframes qkCheckIn{0%{transform:translate(-50%,-50%) scale(0) rotate(-30deg);opacity:0}60%{transform:translate(-50%,-50%) scale(1.1) rotate(0);opacity:1}100%{transform:translate(-50%,-50%) scale(1) rotate(0);opacity:1}}" +
            "@keyframes qkCheckGlow{0%{box-shadow:0 0 15px rgba(108,92,231,.5),0 0 30px rgba(108,92,231,.2)}100%{box-shadow:0 0 25px rgba(108,92,231,.7),0 0 50px rgba(108,92,231,.3)}}" +
            "@keyframes qkTextIn{0%{opacity:0;transform:translateY(10px) scale(.95)}100%{opacity:1;transform:translateY(0) scale(1)}}" +
            "`;document.head.appendChild(s);"
        );
    }

    private void buildDOM() {
        String voteText;
        if (remainingVotes < 0) {
            voteText = "CHECKLIST VOTE RECORDED";
        } else if (remainingVotes > 0) {
            voteText = remainingVotes + " VOTE" + (remainingVotes != 1 ? "S" : "") + " LEFT";
        } else {
            voteText = "NO VOTES LEFT";
        }

        getElement().executeJs(
            "var ov=document.createElement('div');ov.id='qk-overlay';" +
            "ov.style.cssText='position:fixed;inset:0;z-index:99999;pointer-events:none;overflow:hidden;';" +
            "document.body.appendChild(ov);" +
            // Backdrop
            "var bg=document.createElement('div');" +
            "bg.style.cssText='position:absolute;inset:0;background:radial-gradient(ellipse at center,rgba(30,30,47,.9),rgba(30,30,47,.95));opacity:0;animation:qkBgIn .2s ease forwards;';" +
            "ov.appendChild(bg);" +
            // Ring — primary color
            "var ring=document.createElement('div');" +
            "ring.style.cssText='position:absolute;left:50%;top:45%;transform:translate(-50%,-50%);border-radius:50%;border:2px solid rgba(108,92,231,.4);width:10px;height:10px;opacity:0;animation:qkRing .7s cubic-bezier(.22,1,.36,1) forwards .2s;';" +
            "ov.appendChild(ring);" +
            // Checkmark — primary purple
            "var check=document.createElement('div');" +
            "check.style.cssText='position:absolute;left:50%;top:45%;width:70px;height:70px;border-radius:50%;background:linear-gradient(135deg,#6C5CE7,#5A4BD1);transform:translate(-50%,-50%) scale(0);opacity:0;animation:qkCheckIn .4s cubic-bezier(.16,1,.3,1) forwards .25s,qkCheckGlow 1.2s ease-in-out infinite alternate .65s;';" +
            "var svg=document.createElementNS('http://www.w3.org/2000/svg','svg');svg.setAttribute('viewBox','0 0 24 24');svg.setAttribute('width','36');svg.setAttribute('height','36');svg.style.cssText='position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);';" +
            "var path=document.createElementNS('http://www.w3.org/2000/svg','path');path.setAttribute('d','M5 13l4 4L19 7');path.setAttribute('stroke','white');path.setAttribute('stroke-width','3');path.setAttribute('fill','none');path.setAttribute('stroke-linecap','round');path.setAttribute('stroke-linejoin','round');" +
            "svg.appendChild(path);check.appendChild(svg);ov.appendChild(check);" +
            // Text
            "var tw=document.createElement('div');" +
            "tw.style.cssText='position:absolute;left:0;top:58%;width:100%;text-align:center;opacity:0;animation:qkTextIn .35s ease forwards .5s;';" +
            "var txt=document.createElement('div');txt.textContent='" + voteText + "';" +
            "txt.style.cssText='font-family:Courier New,monospace;font-size:clamp(1rem,3.5vw,1.6rem);font-weight:800;letter-spacing:.12em;color:#A29BFE;text-shadow:0 0 20px rgba(108,92,231,.5);white-space:nowrap;';" +
            "tw.appendChild(txt);ov.appendChild(tw);" +
            // Particle canvas
            "var c=document.createElement('canvas');c.id='qk-canvas';" +
            "c.style.cssText='position:absolute;inset:0;width:100%;height:100%;pointer-events:none;';" +
            "ov.appendChild(c);" +
            "qkParticles();"
        );
    }

    private void scheduleSequence(Runnable onComplete) {
        // Fewer particles, project colors
        getElement().executeJs(
            "function qkParticles(){" +
            "var c=document.getElementById('qk-canvas');if(!c)return;" +
            "var ctx=c.getContext('2d');var W=c.width=innerWidth;var H=c.height=innerHeight;" +
            "var cx=W/2,cy=H*.45;var pts=[];var cols=" + arrayToJs(COLORS) + ";" +
            "setTimeout(function(){" +
            "  for(var i=0;i<40;i++){" +
            "    var a=Math.PI*2*i/40+(Math.random()-.5)*.5;" +
            "    var sp=2+Math.random()*3;" +
            "    pts.push({x:cx,y:cy,vx:Math.cos(a)*sp,vy:Math.sin(a)*sp," +
            "      life:30+Math.random()*10,age:0,size:1.5+Math.random()*2," +
            "      color:cols[Math.floor(Math.random()*cols.length)],fric:.97});" +
            "  }" +
            "},300);" +
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

        // Dissolve
        getElement().executeJs(
            "setTimeout(function(){" +
            "  var ov=document.getElementById('qk-overlay');" +
            "  if(ov){ov.style.animation='qkBgOut .5s ease forwards';setTimeout(function(){ov.remove();},700);}" +
            "},1500);"
        );

        UI ui = UI.getCurrent();
        if (ui != null) {
            ui.accessLater(v -> {
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
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
