package com.microslop.views.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;

/**
 * Maximalist vote success animation — a cinematic multi-phase celebration
 * that triggers when any vote (normal or checklist) is submitted.
 *
 * Phases:
 *   1. Screen capture flash + radial shockwave
 *   2. Central orb ignites and explodes into geometric shrapnel
 *   3. "VOTE CAST" chromatic-split text reveal
 *   4. Particle constellation spirals outward
 *   5. Expanding ring pulses echo the moment
 *   6. Graceful dissolve with trailing embers
 */
public class VoteSuccessAnimation extends Div {

    private static final String[] COLORS = {
        "#f59e0b", "#fbbf24", "#fcd34d",
        "#10b981", "#34d399", "#6ee7b7",
        "#6366f1", "#818cf8", "#a5b4fc",
        "#ec4899", "#f472b6", "#f9a8d4",
        "#f43f5e", "#fb7185", "#fda4af"
    };

    private static final String[] SHAPES = {
        "triangle", "diamond", "hexagon", "circle", "cross", "star"
    };

    public VoteSuccessAnimation(Runnable onComplete) {
        buildOverlay();
        injectMasterStyles();
        injectKeyframes();
        buildDOM();
        scheduleSequence(onComplete);
    }

    // ── Overlay ────────────────────────────────────────────────────────

    private void buildOverlay() {
        getElement().executeJs(
            "var ov = document.createElement('div');\n" +
            "ov.id = 'vote-ceremony';\n" +
            "ov.style.cssText = 'position:fixed;inset:0;z-index:99999;pointer-events:none;overflow:hidden;'\n;" +
            "document.body.appendChild(ov);"
        );
    }

    // ── DOM construction via JS for maximum control ────────────────────

    private void buildDOM() {
        getElement().executeJs(
            "var ov = document.getElementById('vote-ceremony');\n" +
            "\n" +
            // Phase 0: dark backdrop
            "var bg = document.createElement('div');\n" +
            "bg.className = 'vc-bg';\n" +
            "ov.appendChild(bg);\n" +
            "\n" +
            // Shockwave rings
            "for (var r = 0; r < 4; r++) {\n" +
            "  var ring = document.createElement('div');\n" +
            "  ring.className = 'vc-ring vc-ring-' + r;\n" +
            "  ov.appendChild(ring);\n" +
            "}\n" +
            "\n" +
            // Central orb
            "var orb = document.createElement('div');\n" +
            "orb.className = 'vc-orb';\n" +
            "var orbInner = document.createElement('div');\n" +
            "orbInner.className = 'vc-orb-inner';\n" +
            "orb.appendChild(orbInner);\n" +
            "var orbGlow = document.createElement('div');\n" +
            "orbGlow.className = 'vc-orb-glow';\n" +
            "orb.appendChild(orbGlow);\n" +
            "ov.appendChild(orb);\n" +
            "\n" +
            // Flash
            "var flash = document.createElement('div');\n" +
            "flash.className = 'vc-flash';\n" +
            "ov.appendChild(flash);\n" +
            "\n" +
            // Text container
            "var txtWrap = document.createElement('div');\n" +
            "txtWrap.className = 'vc-text-wrap';\n" +
            "var txtMain = document.createElement('div');\n" +
            "txtMain.className = 'vc-text vc-text-main';\n" +
            "txtMain.textContent = 'ALL VOTES CAST';\n" +
            "var txtR = document.createElement('div');\n" +
            "txtR.className = 'vc-text vc-text-r';\n" +
            "txtR.textContent = 'ALL VOTES CAST';\n" +
            "var txtG = document.createElement('div');\n" +
            "txtG.className = 'vc-text vc-text-g';\n" +
            "txtG.textContent = 'ALL VOTES CAST';\n" +
            "var txtB = document.createElement('div');\n" +
            "txtB.className = 'vc-text vc-text-b';\n" +
            "txtB.textContent = 'ALL VOTES CAST';\n" +
            "txtWrap.appendChild(txtR);\n" +
            "txtWrap.appendChild(txtG);\n" +
            "txtWrap.appendChild(txtB);\n" +
            "txtWrap.appendChild(txtMain);\n" +
            "\n" +
            "var sub = document.createElement('div');\n" +
            "sub.className = 'vc-sub';\n" +
            "sub.textContent = 'Your voice echoes through the competition';\n" +
            "txtWrap.appendChild(sub);\n" +
            "ov.appendChild(txtWrap);\n" +
            "\n" +
            // Particle canvas
            "var canvas = document.createElement('canvas');\n" +
            "canvas.id = 'vc-particles';\n" +
            "canvas.style.cssText = 'position:absolute;inset:0;width:100%;height:100%;pointer-events:none;';\n" +
            "ov.appendChild(canvas);\n" +
            "\n" +
            // Geometric shrapnel container
            "var shrap = document.createElement('div');\n" +
            "shrap.className = 'vc-shrapnel';\n" +
            "ov.appendChild(shrap);\n" +
            "\n" +
            // Scan lines
            "var scanlines = document.createElement('div');\n" +
            "scanlines.className = 'vc-scanlines';\n" +
            "ov.appendChild(scanlines);\n" +
            "\n" +
            // Dissolve embers container
            "var embers = document.createElement('div');\n" +
            "embers.className = 'vc-embers';\n" +
            "ov.appendChild(embers);\n" +
            "\n" +
            // Initialize particle system\n" +
            "initParticles();\n" +
            "spawnShrapnel();\n" +
            "spawnEmbers();"
        );
    }

    // ── Master stylesheet ──────────────────────────────────────────────

    private void injectMasterStyles() {
        StringBuilder css = new StringBuilder();
        css.append(
            // Background
            ".vc-bg{position:absolute;inset:0;background:radial-gradient(ellipse at center," +
            "rgba(15,23,42,.97) 0%,rgba(2,6,23,.99) 100%);opacity:0;animation:vcBgIn .5s ease forwards .1s;}\n" +

            // Flash
            ".vc-flash{position:absolute;inset:0;background:white;opacity:0;" +
            "animation:vcFlash .6s ease forwards 1.0s;}\n" +

            // Shockwave rings
            ".vc-ring{position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);" +
            "border-radius:50%;border:3px solid rgba(251,191,36,.6);width:10px;height:10px;" +
            "opacity:0;}\n" +
            ".vc-ring-0{animation:vcRingExpand 1.2s cubic-bezier(.22,1,.36,1) forwards 1.1s;}\n" +
            ".vc-ring-1{animation:vcRingExpand 1.4s cubic-bezier(.22,1,.36,1) forwards 1.3s;" +
            "border-color:rgba(16,185,129,.5);}\n" +
            ".vc-ring-2{animation:vcRingExpand 1.6s cubic-bezier(.22,1,.36,1) forwards 1.5s;" +
            "border-color:rgba(99,102,241,.4);}\n" +
            ".vc-ring-3{animation:vcRingExpand 1.8s cubic-bezier(.22,1,.36,1) forwards 1.7s;" +
            "border-color:rgba(236,72,153,.3);}\n" +

            // Central orb
            ".vc-orb{position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);" +
            "width:4px;height:4px;border-radius:50%;opacity:0;" +
            "animation:vcOrbIgnite .8s cubic-bezier(.16,1,.3,1) forwards .7s;}\n" +
            ".vc-orb-inner{position:absolute;inset:0;border-radius:50%;" +
            "background:radial-gradient(circle,#fef3c7,#fbbf24,#f59e0b);}\n" +
            ".vc-orb-glow{position:absolute;inset:-30px;border-radius:50%;" +
            "background:radial-gradient(circle,rgba(251,191,36,.8),rgba(245,158,11,.4),transparent 70%);" +
            "animation:vcOrbPulse 1s ease-in-out infinite alternate .7s;}\n" +

            // Chromatic text
            ".vc-text-wrap{position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);" +
            "text-align:center;opacity:0;animation:vcTextIn .01s forwards 1.6s;}\n" +
            ".vc-text{font-family:'Courier New',monospace;font-size:clamp(2rem,8vw,5rem);" +
            "font-weight:900;letter-spacing:.25em;text-transform:uppercase;white-space:nowrap;}\n" +
            ".vc-text-main{color:#fef3c7;position:relative;z-index:4;" +
            "text-shadow:0 0 40px rgba(251,191,36,.8),0 0 80px rgba(251,191,36,.4)," +
            "0 0 120px rgba(251,191,36,.2);animation:vcTextReveal .8s cubic-bezier(.16,1,.3,1) forwards 1.6s," +
            "vcTextGlow 2s ease-in-out infinite alternate 2.4s;}\n" +
            ".vc-text-r{position:absolute;left:3px;top:0;color:rgba(244,63,94,.7);z-index:1;" +
            "animation:vcChromaR 1.5s cubic-bezier(.16,1,.3,1) forwards 1.6s;}\n" +
            ".vc-text-g{position:absolute;left:-3px;top:0;color:rgba(16,185,129,.7);z-index:2;" +
            "animation:vcChromaG 1.5s cubic-bezier(.16,1,.3,1) forwards 1.6s;}\n" +
            ".vc-text-b{position:absolute;left:0;top:3px;color:rgba(99,102,241,.7);z-index:3;" +
            "animation:vcChromaB 1.5s cubic-bezier(.16,1,.3,1) forwards 1.6s;}\n" +
            ".vc-sub{font-family:'Courier New',monospace;font-size:clamp(.7rem,2vw,1.1rem);" +
            "color:rgba(148,163,184,.9);letter-spacing:.12em;margin-top:1.5rem;" +
            "opacity:0;animation:vcSubIn .8s ease forwards 2.2s;}\n" +

            // Scanlines
            ".vc-scanlines{position:absolute;inset:0;background:repeating-linear-gradient(0deg," +
            "transparent,transparent 2px,rgba(0,0,0,.03) 2px,rgba(0,0,0,.03) 4px);" +
            "pointer-events:none;opacity:0;animation:vcScanIn .3s ease forwards 1.0s;}\n" +

            // Shrapnel
            ".vc-shrapnel{position:absolute;inset:0;pointer-events:none;}\n" +
            ".vc-shard{position:absolute;left:50%;top:50%;width:0;height:0;opacity:0;}\n" +
            ".vc-shard-triangle{width:0;height:0;border-left:8px solid transparent;" +
            "border-right:8px solid transparent;border-bottom:14px solid currentColor;}\n" +
            ".vc-shard-diamond{width:12px;height:12px;background:currentColor;" +
            "transform:translate(-50%,-50%) rotate(45deg);border-radius:2px;}\n" +
            ".vc-shard-hexagon{width:14px;height:8px;background:currentColor;" +
            "transform:translate(-50%,-50%);clip-path:polygon(25% 0%,75% 0%,100% 50%,75% 100%,25% 100%,0% 50%);}\n" +
            ".vc-shard-circle{width:10px;height:10px;background:currentColor;border-radius:50%;}\n" +
            ".vc-shard-cross{width:12px;height:12px;position:relative;}\n" +
            ".vc-shard-cross::before,.vc-shard-cross::after{content:'';position:absolute;background:currentColor;border-radius:1px;}\n" +
            ".vc-shard-cross::before{width:12px;height:3px;top:4.5px;left:0;}\n" +
            ".vc-shard-cross::after{width:3px;height:12px;top:0;left:4.5px;}\n" +
            ".vc-shard-star{width:14px;height:14px;background:currentColor;clip-path:polygon(50% 0%,61% 35%,98% 35%,68% 57%,79% 91%,50% 70%,21% 91%,32% 57%,2% 35%,39% 35%);}\n" +

            // Embers
            ".vc-embers{position:absolute;inset:0;pointer-events:none;}\n" +
            ".vc-ember{position:absolute;border-radius:50%;opacity:0;" +
            "animation:vcEmberFloat var(--dur) ease-out var(--del) forwards;}\n"
        );

        getElement().executeJs(
            "var s=document.createElement('style');s.textContent=$0;document.head.appendChild(s);",
            css.toString()
        );
    }

    // ── Keyframe animations ────────────────────────────────────────────

    private void injectKeyframes() {
        getElement().executeJs(
            "var s=document.createElement('style');s.textContent=`" +
            // Background fade in
            "@keyframes vcBgIn{0%{opacity:0}100%{opacity:1}}" +
            // Flash
            "@keyframes vcFlash{0%{opacity:0}15%{opacity:.9}100%{opacity:0}}" +
            // Shockwave rings
            "@keyframes vcRingExpand{" +
            "  0%{width:10px;height:10px;opacity:.9;border-width:3px}" +
            "  100%{width:min(90vw,700px);height:min(90vw,700px);opacity:0;border-width:1px}" +
            "}" +
            // Orb ignite + explode
            "@keyframes vcOrbIgnite{" +
            "  0%{width:4px;height:4px;opacity:0}" +
            "  30%{width:60px;height:60px;opacity:1}" +
            "  50%{width:80px;height:80px;opacity:1}" +
            "  70%{width:120px;height:120px;opacity:.8}" +
            "  100%{width:0;height:0;opacity:0}" +
            "}" +
            "@keyframes vcOrbPulse{" +
            "  0%{transform:scale(1);opacity:.6}" +
            "  100%{transform:scale(1.8);opacity:.1}" +
            "}" +
            // Text reveal
            "@keyframes vcTextIn{to{opacity:1}}" +
            "@keyframes vcTextReveal{" +
            "  0%{transform:scaleY(0);opacity:0;filter:blur(10px)}" +
            "  50%{transform:scaleY(1.1);opacity:1;filter:blur(0)}" +
            "  100%{transform:scaleY(1);opacity:1;filter:blur(0)}" +
            "}" +
            "@keyframes vcTextGlow{" +
            "  0%{text-shadow:0 0 40px rgba(251,191,36,.8),0 0 80px rgba(251,191,36,.4)}" +
            "  100%{text-shadow:0 0 60px rgba(251,191,36,1),0 0 120px rgba(251,191,36,.6),0 0 200px rgba(251,191,36,.2)}" +
            "}" +
            // Chromatic aberration split + converge
            "@keyframes vcChromaR{" +
            "  0%{transform:translate(15px,-10px);opacity:.9;filter:blur(0)}" +
            "  40%{transform:translate(8px,-5px);opacity:.6;filter:blur(0)}" +
            "  100%{transform:translate(0,0);opacity:0;filter:blur(3px)}" +
            "}" +
            "@keyframes vcChromaG{" +
            "  0%{transform:translate(-15px,10px);opacity:.9;filter:blur(0)}" +
            "  40%{transform:translate(-8px,5px);opacity:.6;filter:blur(0)}" +
            "  100%{transform:translate(0,0);opacity:0;filter:blur(3px)}" +
            "}" +
            "@keyframes vcChromaB{" +
            "  0%{transform:translate(10px,15px);opacity:.9;filter:blur(0)}" +
            "  40%{transform:translate(5px,8px);opacity:.6;filter:blur(0)}" +
            "  100%{transform:translate(0,0);opacity:0;filter:blur(3px)}" +
            "}" +
            // Subtitle
            "@keyframes vcSubIn{0%{opacity:0;transform:translateY(10px)}100%{opacity:1;transform:translateY(0)}}" +
            // Scanlines
            "@keyframes vcScanIn{0%{opacity:0}100%{opacity:.4}}" +
            // Shrapnel fly
            "@keyframes vcShardFly{" +
            "  0%{opacity:1;transform:translate(-50%,-50%) rotate(var(--rot)) scale(1)}" +
            "  100%{opacity:0;transform:translate(calc(-50% + var(--tx)),calc(-50% + var(--ty))) rotate(calc(var(--rot) + 720deg)) scale(.3)}" +
            "}" +
            // Ember float
            "@keyframes vcEmberFloat{" +
            "  0%{opacity:0;transform:translateY(0) scale(1)}" +
            "  20%{opacity:1}" +
            "  100%{opacity:0;transform:translateY(calc(-100px - var(--drift))) scale(0)}" +
            "}" +
            // Dissolve
            "@keyframes vcDissolve{" +
            "  0%{opacity:1;filter:brightness(1)}" +
            "  100%{opacity:0;filter:brightness(2) blur(20px)}" +
            "}" +
            "`;document.head.appendChild(s);"
        );
    }

    // ── Particle system on canvas ──────────────────────────────────────

    private void initParticles() {
        StringBuilder js = new StringBuilder();
        js.append(
            "function initParticles(){\n" +
            "var c=document.getElementById('vc-particles');\n" +
            "if(!c)return;\n" +
            "var ctx=c.getContext('2d');\n" +
            "var W=c.width=window.innerWidth;\n" +
            "var H=c.height=window.innerHeight;\n" +
            "var cx=W/2,cy=H/2;\n" +
            "var particles=[];\n" +
            "var colors=" + arrayToJs(COLORS) + ";\n" +
            "\n" +
            "function spawn(n,opts){\n" +
            "  for(var i=0;i<n;i++){\n" +
            "    var angle=opts.angle!==undefined?opts.angle:(Math.PI*2*i/n)+((Math.random()-.5)*.4);\n" +
            "    var speed=(opts.speed||4)+Math.random()*(opts.speedVar||3);\n" +
            "    particles.push({\n" +
            "      x:opts.x||cx,y:opts.y||cy,\n" +
            "      vx:Math.cos(angle)*speed,vy:Math.sin(angle)*speed,\n" +
            "      life:opts.life||60,age:0,\n" +
            "      size:opts.size||(2+Math.random()*4),\n" +
            "      color:colors[Math.floor(Math.random()*colors.length)],\n" +
            "      trail:opts.trail||false,gravity:opts.gravity||0,\n" +
            "      friction:opts.friction||.98\n" +
            "    });\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            // Phase 1: explosion burst (after flash)
            "setTimeout(function(){\n" +
            "  spawn(120,{speed:6,speedVar:8,size:3,life:80,trail:true,friction:.96});\n" +
            "  spawn(40,{speed:2,speedVar:4,size:6,life:60,friction:.97});\n" +
            "},1100);\n" +
            "\n" +
            // Phase 2: secondary sparkle ring
            "setTimeout(function(){\n" +
            "  for(var i=0;i<30;i++){\n" +
            "    var a=Math.PI*2*i/30;\n" +
            "    spawn(3,{x:cx+Math.cos(a)*60,y:cy+Math.sin(a)*60,angle:a,speed:3,speedVar:2,size:2,life:50});\n" +
            "  }\n" +
            "},1400);\n" +
            "\n" +
            // Phase 3: rising embers after text
            "setTimeout(function(){\n" +
            "  spawn(60,{speed:1,speedVar:2,size:2,life:90,gravity:-.03,friction:.99});\n" +
            "},2000);\n" +
            "\n" +
            // Animation loop\n" +
            "var running=true;\n" +
            "function frame(){\n" +
            "  if(!running)return;\n" +
            "  ctx.clearRect(0,0,W,H);\n" +
            "  for(var i=particles.length-1;i>=0;i--){\n" +
            "    var p=particles[i];\n" +
            "    p.age++;\n" +
            "    if(p.age>p.life){particles.splice(i,1);continue;}\n" +
            "    p.vx*=p.friction;p.vy*=p.friction;p.vy+=p.gravity;\n" +
            "    p.x+=p.vx;p.y+=p.vy;\n" +
            "    var alpha=1-(p.age/p.life);\n" +
            "    ctx.globalAlpha=alpha;\n" +
            "    ctx.fillStyle=p.color;\n" +
            "    ctx.shadowBlur=p.trail?15:0;\n" +
            "    ctx.shadowColor=p.color;\n" +
            "    ctx.beginPath();\n" +
            "    ctx.arc(p.x,p.y,p.size*alpha,0,Math.PI*2);\n" +
            "    ctx.fill();\n" +
            "    if(p.trail){\n" +
            "      ctx.globalAlpha=alpha*.3;\n" +
            "      ctx.beginPath();\n" +
            "      ctx.moveTo(p.x,p.y);\n" +
            "      ctx.lineTo(p.x-p.vx*3,p.y-p.vy*3);\n" +
            "      ctx.strokeStyle=p.color;\n" +
            "      ctx.lineWidth=p.size*.5;\n" +
            "      ctx.stroke();\n" +
            "    }\n" +
            "  }\n" +
            "  ctx.globalAlpha=1;ctx.shadowBlur=0;\n" +
            "  if(particles.length>0){requestAnimationFrame(frame);}\n" +
            "  else{setTimeout(function(){running=false;},500);}\n" +
            "}\n" +
            "setTimeout(frame,50);\n" +
            "}\n"
        );
        getElement().executeJs(js.toString());
    }

    // ── Geometric shrapnel ─────────────────────────────────────────────

    private void spawnShrapnel() {
        StringBuilder js = new StringBuilder();
        js.append("function spawnShrapnel(){\n");
        js.append("var shapes=" + arrayToJs(SHAPES) + ";\n");
        js.append("var colors=" + arrayToJs(COLORS) + ";\n");
        js.append("var el=document.querySelector('.vc-shrapnel');if(!el)return;\n");
        js.append("for(var i=0;i<40;i++){\n");
        js.append("  var s=document.createElement('div');\n");
        js.append("  var shape=shapes[Math.floor(Math.random()*shapes.length)];\n");
        js.append("  var color=colors[Math.floor(Math.random()*colors.length)];\n");
        js.append("  s.className='vc-shard vc-shard-'+shape;\n");
        js.append("  s.style.color=color;\n");
        js.append("  var angle=Math.random()*Math.PI*2;\n");
        js.append("  var dist=150+Math.random()*350;\n");
        js.append("  var tx=Math.cos(angle)*dist;\n");
        js.append("  var ty=Math.sin(angle)*dist;\n");
        js.append("  var rot=Math.random()*360;\n");
        js.append("  var dur=.8+Math.random()*.6;\n");
        js.append("  s.style.setProperty('--tx',tx+'px');\n");
        js.append("  s.style.setProperty('--ty',ty+'px');\n");
        js.append("  s.style.setProperty('--rot',rot+'deg');\n");
        js.append("  s.style.animation='vcShardFly '+dur+'s cubic-bezier(.22,1,.36,1) forwards '+(1.1+Math.random()*.3)+'s';\n");
        js.append("  el.appendChild(s);\n");
        js.append("}\n");
        js.append("setTimeout(function(){el.innerHTML='';},3000);\n");
        js.append("}\n");
        getElement().executeJs(js.toString());
    }

    // ── Floating embers ────────────────────────────────────────────────

    private void spawnEmbers() {
        StringBuilder js = new StringBuilder();
        js.append("function spawnEmbers(){\n");
        js.append("var colors=" + arrayToJs(COLORS) + ";\n");
        js.append("var el=document.querySelector('.vc-embers');if(!el)return;\n");
        js.append("for(var i=0;i<35;i++){\n");
        js.append("  var e=document.createElement('div');\n");
        js.append("  e.className='vc-ember';\n");
        js.append("  var size=2+Math.random()*4;\n");
        js.append("  e.style.width=size+'px';e.style.height=size+'px';\n");
        js.append("  e.style.background=colors[Math.floor(Math.random()*colors.length)];\n");
        js.append("  e.style.left=(10+Math.random()*80)+'%';\n");
        js.append("  e.style.top=(30+Math.random()*60)+'%';\n");
        js.append("  e.style.setProperty('--dur',(1.5+Math.random()*2)+'s');\n");
        js.append("  e.style.setProperty('--del',(2+Math.random()*1.5)+'s');\n");
        js.append("  e.style.setProperty('--drift',(Math.random()*80-40)+'px');\n");
        js.append("  e.style.boxShadow='0 0 '+(size*2)+'px '+e.style.background;\n");
        js.append("  el.appendChild(e);\n");
        js.append("}\n");
        js.append("setTimeout(function(){el.innerHTML='';},5000);\n");
        js.append("}\n");
        getElement().executeJs(js.toString());
    }

    // ── Sequence orchestrator ──────────────────────────────────────────

    private void scheduleSequence(Runnable onComplete) {
        // Dissolve overlay after animation completes
        getElement().executeJs(
            "setTimeout(function(){" +
            "  var ov=document.getElementById('vote-ceremony');" +
            "  if(ov){" +
            "    ov.style.animation='vcDissolve 1.2s ease forwards';" +
            "    setTimeout(function(){ov.remove();},1500);" +
            "  }" +
            "},3800);"
        );

        // Trigger callback after full sequence
        UI ui = UI.getCurrent();
        if (ui != null) {
            ui.accessLater(v -> {
                try { Thread.sleep(4800); } catch (InterruptedException ignored) {}
                onComplete.run();
            }, () -> {});
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────

    private static String arrayToJs(String[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append("'").append(arr[i]).append("'");
            if (i < arr.length - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }
}
