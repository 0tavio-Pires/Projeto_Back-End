import type {Network,Train} from './types';
export const movement:Record<string,string>={STOPPED:'Fora de circulação',DWELLING:'Na plataforma',MOVING:'Em movimento',WAITING:'Aguardando via',BLOCKED:'Bloqueado'};
export const severity:Record<string,string>={INFO:'Informativa',LOW:'Baixa',MEDIUM:'Média',HIGH:'Alta',CRITICAL:'Crítica'};
export const effect:Record<string,string>={NOTICE:'Aviso',SLOW:'Velocidade reduzida',BLOCK:'Bloqueio'};
export const level:Record<string,string>={DIRECT:'Impacto direto',OPERATIONAL:'Dependência operacional',POTENTIAL:'Risco por integração'};
export function duration(value:number){const n=Math.floor(value);return `${Math.floor(n/3600).toString().padStart(2,'0')}:${Math.floor(n%3600/60).toString().padStart(2,'0')}:${(n%60).toString().padStart(2,'0')}`;}
export function location(n:Network,t:Train){if(t.nodeId)return n.stations[n.nodes[t.nodeId].stationId].name;const e=n.edges[t.edgeId!];return `${n.stations[n.nodes[e.from].stationId].name} → ${n.stations[n.nodes[e.to].stationId].name}`;}
export function position(n:Network,t:Train):[number,number]{
  if(t.nodeId){const s=n.stations[n.nodes[t.nodeId].stationId];return [s.x,s.y];}
  const e=n.edges[t.edgeId!],a=n.stations[n.nodes[e.from].stationId],b=n.stations[n.nodes[e.to].stationId],p=Math.max(0,Math.min(1,t.progressMeters/e.lengthMeters));
  return [a.x+(b.x-a.x)*p,a.y+(b.y-a.y)*p];
}
export function visibleTrains(n:Network,line:string,query:string){const q=query.trim().toLocaleLowerCase('pt-BR');return Object.values(n.trains).filter(t=>(!line||t.currentLineId===line)&&(!q||`${t.id} ${t.number} ${t.model} ${location(n,t)}`.toLocaleLowerCase('pt-BR').includes(q)));}
