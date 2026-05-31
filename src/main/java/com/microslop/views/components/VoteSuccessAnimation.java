package com.microslop.views.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;

/**
 * Final vote animation — refined and cohesive with Votify's design system.
 * Uses project colors (primary purple, secondary teal, accent pink).
 * No flashbang. Smooth, satisfying, not overwhelming.
 */
public class VoteSuccessAnimation extends Div {

    public VoteSuccessAnimation(Runnable onComplete) {
        injectStyles();
        buildDOM();
        scheduleSequence(onComplete);
    }

    private void injectStyles() {
        getElement().executeJs(
            "var s=document.createElement('style');s.textContent=`" +
            "@keyframes vfBgIn{0%{opacity:0}100%{opacity:1}}" +
            "@keyframes vfBgOut{0%{opacity:1}100%{opacity:0}}" +
            "@keyframes vfRing{0%{width:10px;height:10px;opacity:.6;border-width:2px}100%{width:min(70vw,550px);height:min(70vw,550px);opacity:0;border-width:1px}}" +
            "@keyframes vfOrbIn{0%{width:0;height:0;opacity:0}40%{width:60px;height:60px;opacity:1}70%{width:70px;height:70px;opacity:.9}100%{width:0;height:0;opacity:0}}" +
            "@keyframes vfOrbGlow{0%{transform:scale(1);opacity:.4}100%{transform:scale(1.6);opacity:0}}" +
            "@keyframes vfTextIn{0%{opacity:0;transform:scaleY(0);filter:blur(6px)}100%{opacity:1;transform:scaleY(1);filter:blur(0)}}" +
            "@keyframes vfTextGlow{0%{text-shadow:0 0 20px rgba(108,92,231,.5)}100%{text-shadow:0 0 40px rgba(108,92,231,.8),0 0 80px rgba(108,92,231,.3)}}" +
            "@keyframes vfSubIn{0%{opacity:0;transform:translateY(8px)}100%{opacity:1;transform:translateY(0)}}" +
            "@keyframes vfCR{0%{transform:translate(8px,-6px);opacity:.6}100%{transform:translate(0,0);opacity:0}}" +
            "@keyframes vfCG{0%{transform:translate(-8px,6px);opacity:.6}100%{transform:translate(0,0);opacity:0}}" +
            "@keyframes vfCB{0%{transform:translate(6px,8px);opacity:.6}100%{transform:translate(0,0);opacity:0}}" +
            "`;document.head.appendChild(s);"
        );
    }

    private void buildDOM() {
        getElement().executeJs(
            "var ov=document.createElement('div');ov.id='vf-overlay';" +
            "ov.style.cssText='position:fixed;inset:0;z-index:99999;pointer-events:none;overflow:hidden;';" +
            "document.body.appendChild(ov);" +
            // Backdrop — dark but not opaque
            "var bg=document.createElement('div');" +
            "bg.style.cssText='position:absolute;inset:0;background:radial-gradient(ellipse at center,rgba(30,30,47,.92),rgba(30,30,47,.97));opacity:0;animation:vfBgIn .35s ease forwards .05s;';" +
            "ov.appendChild(bg);" +
            // Rings — using primary color
            "for(var r=0;r<3;r++){" +
            "  var ring=document.createElement('div');" +
            "  var delays=['.6s','.8s','1s'];" +
            "  var colors=['rgba(108,92,231,.4)','rgba(0,206,201,.3)','rgba(253,121,168,.25)'];" +
            "  ring.style.cssText='position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);border-radius:50%;border:2px solid '+colors[r]+';width:10px;height:10px;opacity:0;animation:vfRing 1.2s cubic-bezier(.22,1,.36,1) forwards '+delays[r]+';';" +
            "  ov.appendChild(ring);" +
            "}" +
            // Orb — primary purple glow
            "var orb=document.createElement('div');" +
            "orb.style.cssText='position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);width:0;height:0;border-radius:50%;opacity:0;animation:vfOrbIn .8s cubic-bezier(.16,1,.3,1) forwards .4s;';" +
            "var orbInner=document.createElement('div');" +
            "orbInner.style.cssText='position:absolute;inset:0;border-radius:50%;background:radial-gradient(circle,#A29BFE,#6C5CE7,#5A4BD1);';" +
            "var orbGlow=document.createElement('div');" +
            "orbGlow.style.cssText='position:absolute;inset:-20px;border-radius:50%;background:radial-gradient(circle,rgba(108,92,231,.6),rgba(108,92,231,.2),transparent 70%);animation:vfOrbGlow 1s ease-in-out infinite alternate .4s;';" +
            "orb.appendChild(orbInner);orb.appendChild(orbGlow);ov.appendChild(orb);" +
            // Text container
            "var tw=document.createElement('div');" +
            "tw.style.cssText='position:absolute;left:0;top:50%;width:100%;transform:translateY(-50%);text-align:center;opacity:0;animation:vfTextIn .01s forwards 1.1s;';" +
            // Chromatic layers
            "var msg='ALL VOTES CAST';" +
            "var sub='Your voice echoes through the competition';" +
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
            "    d.style.animation='vfTextIn .6s cubic-bezier(.16,1,.3,1) forwards 1.1s,vfTextGlow 2s ease-in-out infinite alternate 1.7s;';" +
            "  }" +
            "  return d;" +
            "}" +
            "textStack.appendChild(mkText('r','animation:vfCR 1s ease forwards 1.1s;z-index:1;'));" +
            "textStack.appendChild(mkText('g','animation:vfCG 1s ease forwards 1.1s;z-index:2;'));" +
            "textStack.appendChild(mkText('b','animation:vfCB 1s ease forwards 1.1s;z-index:3;'));" +
            "textStack.appendChild(mkText(null,''));" +
            "tw.appendChild(textStack);" +
            "var subEl=document.createElement('div');subEl.textContent=sub;" +
            "subEl.style.cssText='font-family:Courier New,monospace;font-size:clamp(.65rem,1.8vw,.95rem);color:rgba(148,163,184,.8);letter-spacing:.08em;margin-top:1rem;position:relative;clear:both;opacity:0;animation:vfSubIn .5s ease forwards 1.6s;';" +
            "tw.appendChild(subEl);ov.appendChild(tw);" +
            // Particle canvas — fewer particles
            "var c=document.createElement('canvas');c.id='vf-canvas';" +
            "c.style.cssText='position:absolute;inset:0;width:100%;height:100%;pointer-events:none;';" +
            "ov.appendChild(c);" +
            "vfParticles();"
        );
    }

    private void scheduleSequence(Runnable onComplete) {
        // Particle system — reduced count, project colors
        getElement().executeJs(
            "function vfParticles(){" +
            "var c=document.getElementById('vf-canvas');if(!c)return;" +
            "var ctx=c.getContext('2d');var W=c.width=innerWidth;var H=c.height=innerHeight;" +
            "var cx=W/2,cy=H/2;var pts=[];" +
            "var cols=['#6C5CE7','#A29BFE','#00CEC9','#FD79A8','#5A4BD1'];" +
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
            // Burst — 60 particles instead of 120
            "setTimeout(function(){spawn(60,{sp:4,sv:4,sz:2,l:55,t:true,f:.97});},600);" +
            // Ring sparks — fewer
            "setTimeout(function(){for(var i=0;i<12;i++){var a=Math.PI*2*i/12;spawn(2,{x:cx+Math.cos(a)*35,y:cy+Math.sin(a)*35,a:a,sp:2,sv:1,sz:1.5,l:35});}},800);" +
            // Rising embers — fewer
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

        // Dissolve
        getElement().executeJs(
            "setTimeout(function(){" +
            "  var ov=document.getElementById('vf-overlay');" +
            "  if(ov){ov.style.animation='vfBgOut .8s ease forwards';setTimeout(function(){ov.remove();},1000);}" +
            "},3200);"
        );

        UI ui = UI.getCurrent();
        if (ui != null) {
            java.util.concurrent.CompletableFuture.runAsync(() -> {
                try { Thread.sleep(3800); } catch (InterruptedException ignored) {}
                ui.access(onComplete::run);
            });
        }
    }
}
