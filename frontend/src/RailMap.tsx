import {useMemo,useState} from 'react';
import type {Network,Impact} from './types';
import {position,visibleTrains} from './utils';

export default function RailMap({n,impacts,line,query,selected,onTrain,onStation}:{n:Network;impacts:Impact[];line:string;query:string;selected:string;onTrain:(id:string)=>void;onStation:(id:string)=>void}){
  const [zoom,setZoom]=useState(1),[pan,setPan]=useState({x:0,y:0}),[drag,setDrag]=useState<{x:number;y:number;px:number;py:number}|null>(null);
  const [hover,setHover]=useState('');
  const stations=useMemo(()=>new Set(Object.values(n.nodes).filter(v=>!line||v.lineId===line).map(v=>v.stationId)),[n.nodes,line]);
  const tracks=useMemo(()=>{
    const seen=new Set<string>();
    return Object.values(n.edges).filter(e=>{
      const from=n.nodes[e.from],to=n.nodes[e.to];
      const key=[from.lineId,to.lineId].sort().join(':')+'|'+[from.stationId,to.stationId].sort().join(':');
      if(seen.has(key))return false;seen.add(key);return true;
    });
  },[n.edges,n.nodes]);
  const major=new Set(['LUZ','BRAS','SE','REPUBLICA','PINHEIROS','SANTO-AMARO','TUCURUVI','JUNDIAI','ESTUDANTES','VILA-PRUDENTE','OSASCO','AEROPORTO-GUARULHOS']);
  const trains=visibleTrains(n,line,query).filter(t=>t.active);
  return <div className="map-shell">
    <div className="map-caption"><span className="live-dot"/>{line?n.lines[line].name:'REDE METROPOLITANA'}<small>São Paulo · cenário histórico 2024</small></div>
    <div className="map-tools"><button title="Ampliar mapa" onClick={()=>setZoom(Math.min(4,zoom+.3))}>+</button><button title="Reduzir mapa" onClick={()=>setZoom(Math.max(.7,zoom-.3))}>−</button><button title="Centralizar mapa" onClick={()=>{setZoom(1);setPan({x:0,y:0});}}>⌖</button></div>
    <svg className="rail-map" viewBox={`${pan.x+(1800-1800/zoom)/2} ${pan.y+(1190-1190/zoom)/2} ${1800/zoom} ${1190/zoom}`} aria-label="Mapa da rede ferroviária e composições em circulação"
      onPointerDown={e=>{if(e.target===e.currentTarget){e.currentTarget.setPointerCapture(e.pointerId);setDrag({x:e.clientX,y:e.clientY,px:pan.x,py:pan.y});}}}
      onPointerMove={e=>{if(drag){const scale=1800/zoom/e.currentTarget.getBoundingClientRect().width;setPan({x:drag.px-(e.clientX-drag.x)*scale,y:drag.py-(e.clientY-drag.y)*scale});}}}
      onPointerUp={()=>setDrag(null)} onPointerCancel={()=>setDrag(null)}>
      <defs><pattern id="grid" width="35" height="35" patternUnits="userSpaceOnUse"><circle cx="1" cy="1" r="1" fill="#26364b"/></pattern></defs>
      <rect x="-10000" y="-10000" width="20000" height="20000" fill="url(#grid)" pointerEvents="none"/>
      {tracks.map(e=>{
        const a=n.stations[n.nodes[e.from].stationId],b=n.stations[n.nodes[e.to].stationId],l=n.lines[n.nodes[e.from].lineId];
        const blocked=!e.enabled||!l.enabled||Object.values(n.incidents).some(i=>i.status!=='RESOLVED'&&i.effect==='BLOCK'&&((i.targetType==='EDGE'&&i.targetId===e.id)||(i.targetType==='LINE'&&i.targetId===l.id)));
        return <line key={e.id} x1={a.x} y1={a.y} x2={b.x} y2={b.y} stroke={blocked?'#f87171':e.connection?'#fff':l.color} strokeWidth={e.connection?5:7} strokeDasharray={blocked||e.connection?'9 7':undefined} opacity={line&&line!==l.id?.toString()? .14:.8} strokeLinecap="round"><title>{l.name} · {a.name} → {b.name}{blocked?' · Bloqueado':''}</title></line>;
      })}
      {Object.values(n.stations).filter(s=>stations.has(s.id)).map(s=>{
        const alert=Object.values(n.incidents).some(i=>i.status!=='RESOLVED'&&i.targetType==='STATION'&&i.targetId===s.id);
        return <g key={s.id} className="station-node" tabIndex={0} role="button" aria-label={`Estação ${s.name}`} onFocus={()=>setHover(s.id)} onBlur={()=>setHover('')} onMouseEnter={()=>setHover(s.id)} onMouseLeave={()=>setHover('')} onClick={()=>onStation(s.id)} onKeyDown={e=>{if(e.key==='Enter')onStation(s.id);}}>
          {alert&&<circle cx={s.x} cy={s.y} r="13" fill="#fb718540" stroke="#fb7185"/>}
          <circle cx={s.x} cy={s.y} r={major.has(s.id)?6:4} fill={s.enabled?'#0c1728':'#ef4444'} stroke="#bac9dd" strokeWidth="2"/>
          {(major.has(s.id)||zoom>1.6||hover===s.id)&&<text x={s.x+10} y={s.y-10} className="station-label" fontSize={hover===s.id?20:16}>{s.name}</text>}
          <title>{s.name}{!s.enabled?' · Desativada':''}</title>
        </g>;
      })}
      {trains.map(t=>{const [x,y]=position(n,t),color=n.lines[t.currentLineId].color;const offset=n.nodes[t.nodeId??n.edges[t.edgeId!].from].direction==='A'?-13:13;
        return <g key={t.id} transform={`translate(${x},${y+offset})`} className="train-marker" role="button" tabIndex={0} aria-label={`Composição ${t.number}, ${t.id}`} onClick={()=>onTrain(t.id)} onKeyDown={e=>{if(e.key==='Enter')onTrain(t.id);}}>
          {selected===t.id&&<rect x="-26" y="-17" width="52" height="34" rx="12" fill="none" stroke="#fff" strokeWidth="2"/>}
          <rect x="-22" y="-12" width="44" height="24" rx="7" fill={color} stroke={t.movement==='BLOCKED'?'#fb7185':'#0b1220'} strokeWidth="3"/>
          <text textAnchor="middle" y="4" fill="#07101c" fontSize="13" fontWeight="800">{t.number}</text><title>{t.id} · {t.number} · {t.reason||t.movement}</title>
        </g>;
      })}
    </svg>
    <div className="map-footer"><span><i className="legend-track"/>Via direcional</span><span><i className="legend-train"/>Composição identificada</span><span className="muted">Arraste para navegar · use + para detalhes</span></div>
    {impacts.length>0&&<div className="map-alert">△ {new Set(impacts.map(i=>i.lineId)).size} linhas com impacto ou risco sinalizado</div>}
  </div>;
}
