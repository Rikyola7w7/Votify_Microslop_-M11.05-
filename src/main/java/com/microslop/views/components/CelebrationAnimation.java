package com.microslop.views.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;

/**
 * Celebration animation — matches Votify's design system.
 * Primary purple/gold orb, smooth chromatic text, refined particles.
 * No flashbang. Elegant and cohesive.
 */
public class CelebrationAnimation extends Div {

    private static final String[] COLORS = {
        "#6C5CE7", "#A29BFE", "#00CEC9", "#FD79A8", "#5A4BD1", "#00B894"
    };

    private final String message;
    private final String subtitle;

    public CelebrationAnimation(String message, String subtitle, Runnable onComplete) {
        this.message = message;
        this.subtitle = subtitle;
        injectStyles();
        buildDOM();
        scheduleParticles();
        // Dissolve after sequence
        getElement().executeJs(
            "setTimeout(function(){" +
            "  var ov=document.getElementById('cel-overlay');" +
            "  if(ov){ov.style.animation='celBgOut .8s ease forwards';setTimeout(function(){ov.remove();},1000);}" +
            "},3200);"
        );
    }

    private void injectStyles() {
        getElement().executeJs(
            "var s=document.createElement('style');s.textContent=`" +
            "@keyframes celBgIn{0%{opacity:0}100%{opacity:1}}" +
            "@keyframes celBgOut{0%{opacity:1}100%{opacity:0}}" +
            "@keyframes celRing{0%{width:10px;height:10px;opacity:.5;border-width:2px}100%{width:min(65vw,500px);height:min(65vw,500px);opacity:0;border-width:1px}}" +
            "@keyframes celOrbIn{0%{width:0;height:0;opacity:0}35%{width:55px;height:55px;opacity:1}65%{width:65px;height:65px;opacity:.9}100%{width:0;height:0;opacity:0}}" +
            "@keyframes celOrbGlow{0%{transform:scale(1);opacity:.4}100%{transform:scale(1.5);opacity:0}}" +
            "@keyframes celTextIn{0%{opacity:0;transform:scaleY(0);filter:blur(5px)}100%{opacity:1;transform:scaleY(1);filter:blur(0)}}" +
            "@keyframes celTextGlow{0%{text-shadow:0 0 20px rgba(108,92,231,.5)}100%{text-shadow:0 0 35px rgba(108,92,231,.7),0 0 70px rgba(108,92,231,.3)}}" +
            "@keyframes celSubIn{0%{opacity:0;transform:translateY(8px)}100%{opacity:1;transform:translateY(0)}}" +
            "@keyframes celCR{0%{transform:translate(8px,-6px);opacity:.5}100%{transform:translate(0,0);opacity:0}}" +
            "@keyframes celCG{0%{transform:translate(-8px,6px);opacity:.5}100%{transform:translate(0,0);opacity:0}}" +
            "@keyframes celCB{0%{transform:translate(6px,8px);opacity:.5}100%{transform:translate(0,0);opacity:0}}" +
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
            // Backdrop
            "var bg=document.createElement('div');" +
            "bg.style.cssText='position:absolute;inset:0;background:radial-gradient(ellipse at center,rgba(30,30,47,.92),rgba(30,30,47,.97));opacity:0;animation:celBgIn .35s ease forwards .05s;';" +
            "ov.appendChild(bg);" +
            // Rings
            "for(var r=0;r<3;r++){" +
            "  var ring=document.createElement('div');" +
            "  var delays=['.6s','.8s','1s'];" +
            "  var colors=['rgba(108,92,231,.4)','rgba(0,206,201,.3)','rgba(253,121,168,.25)'];" +
            "  ring.style.cssText='position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);border-radius:50%;border:2px solid '+colors[r]+';width:10px;height:10px;opacity:0;animation:celRing 1.2s cubic-bezier(.22,1,.36,1) forwards '+delays[r]+';';" +
            "  ov.appendChild(ring);" +
            "}" +
            // Orb — primary purple
            "var orb=document.createElement('div');" +
            "orb.style.cssText='position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);width:0;height:0;border-radius:50%;opacity:0;animation:celOrbIn .8s cubic-bezier(.16,1,.3,1) forwards .4s;';" +
            "var orbInner=document.createElement('div');" +
            "orbInner.style.cssText='position:absolute;inset:0;border-radius:50%;background:radial-gradient(circle,#A29BFE,#6C5CE7,#5A4BD1);';" +
            "var orbGlow=document.createElement('div');" +
            "orbGlow.style.cssText='position:absolute;inset:-20px;border-radius:50%;background:radial-gradient(circle,rgba(108,92,231,.6),rgba(108,92,231,.2),transparent 70%);animation:celOrbGlow 1s ease-in-out infinite alternate .4s;';" +
            "orb.appendChild(orbInner);orb.appendChild(orbGlow);ov.appendChild(orb);" +
            // Text container
            "var tw=document.createElement('div');" +
            "tw.style.cssText='position:absolute;left:0;top:50%;width:100%;transform:translateY(-50%);text-align:center;opacity:0;animation:celTextIn .01s forwards 1.1s;';" +
            // Chromatic layers
            "var msg='" + escapedMsg + "';" +
            "var sub='" + escapedSub + "';" +
            "var textStack=document.createElement('div');" +
            "textStack.style.cssText='position:relative;display:inline-block;';" +
            "var spacer=document.createElement('div');spacer.textContent=msg;" +
            "spacer.style.cssText='font-family:Courier New,monospace;font-size:clamp(1.6rem,6vw,3.5rem);font-weight:900;letter-spacing:.18em;white-space:nowrap;visibility:hidden;';" +
            "textStack.appendChild(spacer);" +
            "function mkText(cls,anim){" +
            "  var d=document.createElement('div');d.textContent=msg;" +
            "  d.style.cssText='font-family:Courier New,monospace;font-size:clamp(1.6rem,6vw,3.5rem);font-weight:900;letter-spacing:.18em;white-space:nowrap;position:absolute;left:0;top:0;width:100%;'+anim;" +
            "  if(cls){" +
            "    var cols={r:'rgba(253,121,168,.5)',g:'rgba(0,206,201,.5)',b:'rgba(108,92,231,.5)'};" +
            "    d.style.color=cols[cls];" +
            "  } else {" +
            "    d.style.color='#A29BFE';d.style.zIndex='4';" +
            "    d.style.textShadow='0 0 30px rgba(108,92,231,.6),0 0 60px rgba(108,92,231,.3)';" +
            "    d.style.animation='celTextIn .6s cubic-bezier(.16,1,.3,1) forwards 1.1s,celTextGlow 2s ease-in-out infinite alternate 1.7s;';" +
            "  }" +
            "  return d;" +
            "}" +
            "textStack.appendChild(mkText('r','animation:celCR 1s ease forwards 1.1s;z-index:1;'));" +
            "textStack.appendChild(mkText('g','animation:celCG 1s ease forwards 1.1s;z-index:2;'));" +
            "textStack.appendChild(mkText('b','animation:celCB 1s ease forwards 1.1s;z-index:3;'));" +
            "textStack.appendChild(mkText(null,''));" +
            "tw.appendChild(textStack);" +
            // Subtitle
            "var subEl=document.createElement('div');subEl.textContent=sub;" +
            "subEl.style.cssText='font-family:Courier New,monospace;font-size:clamp(.65rem,1.8vw,.95rem);color:rgba(148,163,184,.8);letter-spacing:.08em;margin-top:1rem;position:relative;clear:both;opacity:0;animation:celSubIn .5s ease forwards 1.6s;';" +
            "tw.appendChild(subEl);ov.appendChild(tw);" +
            // Particle canvas
            "var c=document.createElement('canvas');c.id='cel-canvas';" +
            "c.style.cssText='position:absolute;inset:0;width:100%;height:100%;pointer-events:none;';" +
            "ov.appendChild(c);" +
            "celParticles();"
        );
    }

    private void scheduleParticles() {
        getElement().executeJs(
            "function celParticles(){" +
            "var c=document.getElementById('cel-canvas');if(!c)return;" +
            "var ctx=c.getContext('2d');var W=c.width=innerWidth;var H=c.height=innerHeight;" +
            "var cx=W/2,cy=H/2;var pts=[];" +
            "var cols=" + arrayToJs(COLORS) + ";" +
            "function spawn(n,o){" +
            "  for(var i=0;i<n;i++){" +
            "    var a=o.a!==undefined?o.a:(Math.PI*2*i/n)+((Math.random()-.5)*.5);" +
            "    var sp=(o.sp||3)+Math.random()*(o.sv||2);" +
            "    pts.push({x:o.x||cx,y:o.y||cy,vx:Math.cos(a)*sp,vy:Math.sin(a)*sp," +
            "      life:o.l||50,age:0,size:o.sz||(2+Math.random()*2.5)," +
            "      color:cols[Math.floor(Math.random()*cols.length)],trail:o.t||false," +
            "      grav:o.g||0,fric:o.f||.97});" +
            "  }" +
            "}" +
            // Burst — 60 particles
            "setTimeout(function(){spawn(60,{sp:4,sv:4,sz:2,l:55,t:true,f:.97});},600);" +
            // Ring sparks — 12
            "setTimeout(function(){for(var i=0;i<12;i++){var a=Math.PI*2*i/12;spawn(2,{x:cx+Math.cos(a)*35,y:cy+Math.sin(a)*35,a:a,sp:2,sv:1,sz:1.5,l:35});}},800);" +
            // Rising embers — 25
            "setTimeout(function(){spawn(25,{sp:.6,sv:1,sz:1.5,l:60,g:-.02,f:.99});},1200);" +
            "var run=true;function frame(){" +
            "  if(!run)return;ctx.clearRect(0,0,W,H);" +
            "  for(var i=pts.length-1;i>=0;i--){" +
            "    var p=pts[i];p.age++;if(p.age>p.life){pts.splice(i,1);continue;}" +
            "    p.vx*=p.fric;p.vy*=p.fric;p.vy+=p.grav;p.x+=p.vx;p.y+=p.vy;" +
            "    var al=1-(p.age/p.life);ctx.globalAlpha=al;ctx.fillStyle=p.color;" +
            "    ctx.shadowBlur=p.trail?10:0;ctx.shadowColor=p.color;" +
            "    ctx.beginPath();ctx.arc(p.x,p.y,p.size*al,0,Math.PI*2);ctx.fill();" +
            "    if(p.trail){ctx.globalAlpha=al*.2;ctx.beginPath();ctx.moveTo(p.x,p.y);" +
            "      ctx.lineTo(p.x-p.vx*2,p.y-p.vy*2);ctx.strokeStyle=p.color;ctx.lineWidth=p.size*.3;ctx.stroke();}" +
            "  }" +
            "  ctx.globalAlpha=1;ctx.shadowBlur=0;" +
            "  if(pts.length>0)requestAnimationFrame(frame);else run=false;" +
            "}" +
            "setTimeout(frame,40);}"
        );
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
