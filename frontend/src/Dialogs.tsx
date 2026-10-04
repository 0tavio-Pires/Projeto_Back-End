import {createContext,useContext,useEffect,useRef,useState} from 'react';
import type {FormEvent,ReactNode} from 'react';
import type {Action,Network,Route,Train} from './types';
import {request} from './api';
import {effect,severity} from './utils';

export const OperationError=createContext('');
export function Modal({title,children,close}:{title:string;children:ReactNode;close:()=>void}){
  const error=useContext(OperationError);
  const ref=useRef<HTMLDialogElement>(null);
  useEffect(()=>{const dialog=ref.current!,previous=document.activeElement as HTMLElement;dialog.showModal();return()=>{dialog.close();previous?.focus();};},[]);
  return <dialog ref={ref} onCancel={e=>{e.preventDefault();close();}} aria-labelledby="dialog-title"><div className="dialog-head"><div><span className="eyebrow">CENTRO DE OPERAÇÕES</span><h2 id="dialog-title">{title}</h2></div><button className="icon-button" aria-label="Fechar" onClick={close}>×</button></div>{error&&<p className="error-box" role="alert">{error}</p>}{children}</dialog>;
}
export function TrainDialog({n,train,action,close,busy}:{n:Network;train?:Train;action:Action;close:()=>void;busy:boolean}){
  const [line,setLine]=useState(train?.currentLineId??Object.keys(n.lines)[0]);
  async function submit(e:FormEvent<HTMLFormElement>){e.preventDefault();const f=new FormData(e.currentTarget);const body={id:f.get('id'),number:f.get('number'),model:f.get('model'),profileId:f.get('profileId'),capacity:Number(f.get('capacity')),carriages:Number(f.get('carriages')),lineId:line,nodeId:f.get('nodeId'),active:f.has('active')};if(await action(`/trains${train?'/'+encodeURIComponent(train.id):''}`,body,train?'PUT':'POST'))close();}
  return <Modal title={train?'Editar composição':'Colocar a rede em movimento'} close={close}><p className="muted">Cada composição recebe uma identificação única. A plataforma de entrada precisa estar livre e o perfil técnico deve ser compatível.</p><form onSubmit={submit} className="form-grid">
    <label>Identificador<input name="id" required maxLength={100} pattern="[A-Za-z0-9_.:\-]+" defaultValue={train?.id??`T-${Date.now().toString(36).toUpperCase()}`} readOnly={!!train}/></label>
    <label>Número da composição<input name="number" required maxLength={40} placeholder="Ex.: 8501" defaultValue={train?.number}/></label>
    <label className="span-2">Modelo<input name="model" required maxLength={120} defaultValue={train?.model??'Composição de simulação'}/></label>
    <label>Linha de entrada<select value={line} onChange={e=>setLine(e.target.value)}>{Object.values(n.lines).map(l=><option key={l.id} value={l.id}>{l.name}</option>)}</select></label>
    <label>Perfil técnico<select name="profileId" key={line} defaultValue={train?.profileId??n.lines[line]?.profileId}>{Object.values(n.profiles).map(p=><option key={p.id} value={p.id}>{p.name}</option>)}</select></label>
    <label className="span-2">Plataforma e sentido<select name="nodeId" key={`node-${line}`} defaultValue={train?.nodeId??undefined}>{Object.values(n.nodes).filter(v=>v.lineId===line).map(v=><option key={v.id} value={v.id}>{n.stations[v.stationId].name} · sentido {v.direction}</option>)}</select></label>
    <label>Capacidade de passageiros<input name="capacity" type="number" min="1" max="10000" defaultValue={train?.capacity??1200} required/></label>
    <label>Carros<input name="carriages" type="number" min="1" max="30" defaultValue={train?.carriages??6} required/></label>
    <label className="check span-2"><input name="active" type="checkbox" defaultChecked={train?.active??true}/>Entrar em circulação ao salvar</label>
    <div className="dialog-actions span-2"><button type="button" onClick={close}>Cancelar</button><button className="primary" disabled={busy}>Salvar composição</button></div>
  </form></Modal>;
}
export function TransferDialog({n,train,action,close,busy}:{n:Network;train:Train;action:Action;close:()=>void;busy:boolean}){
  const [line,setLine]=useState(Object.keys(n.lines).find(id=>id!==train.currentLineId)??train.currentLineId),[destination,setDestination]=useState(''),[route,setRoute]=useState<Route|null>(null),[error,setError]=useState(''),[loading,setLoading]=useState(false);
  async function plan(){setError('');setRoute(null);setLoading(true);try{setRoute(await request<Route>(`/api/v1/trains/${encodeURIComponent(train.id)}/route`,{targetLineId:line,destinationNodeId:destination||null}));}catch(e){setError((e as Error).message);}finally{setLoading(false);}}
  return <Modal title={`Transferir composição ${train.number}`} close={close}><p className="muted">A simulação fica pausada para planejar. A rota usa vias direcionais verificadas, compatibilidade técnica, bloqueios e tempo estimado de espera.</p><div className="form-grid">
    <label className="span-2">Linha de destino<select value={line} onChange={e=>{setLine(e.target.value);setDestination('');setRoute(null);}}>{Object.values(n.lines).map(l=><option key={l.id} value={l.id}>{l.name}</option>)}</select></label>
    <label className="span-2">Destino<select value={destination} onChange={e=>{setDestination(e.target.value);setRoute(null);}}><option value="">Primeira plataforma acessível na linha</option>{Object.values(n.nodes).filter(v=>v.lineId===line).map(v=><option key={v.id} value={v.id}>{n.stations[v.stationId].name} · {v.direction}</option>)}</select></label>
    <button className="span-2" onClick={plan} disabled={loading||busy}>{loading?'Calculando…':'Calcular melhor caminho'}</button>
  </div>{error&&<p className="error-box" role="alert">{error}</p>}{route&&<div className="route-result"><div className="route-summary"><strong>{Math.ceil(route.estimatedSeconds/60)} min <small>estimados</small></strong><span>{route.edgeIds.length} trechos · {route.lineChanges} mudanças de linha</span></div><ol>{route.steps.map((step,i)=><li key={i}>{step}</li>)}</ol></div>}
    <div className="dialog-actions"><button onClick={close}>Fechar</button><button className="primary" disabled={!route||busy} onClick={async()=>{if(route&&await action(`/trains/${encodeURIComponent(train.id)}/transfer`,{targetLineId:line,destinationNodeId:route.destinationNodeId,expectedVersion:route.stateVersion}))close();}}>Autorizar transferência</button></div>
  </Modal>;
}
export function IncidentDialog({n,action,close,busy,target}:{n:Network;action:Action;close:()=>void;busy:boolean;target?:{type:string;id:string}}){
  const [type,setType]=useState(target?.type??'LINE');
  const targets=type==='LINE'?Object.values(n.lines).map(v=>[v.id,v.name]):type==='STATION'?Object.values(n.stations).map(v=>[v.id,v.name]):type==='TRAIN'?Object.values(n.trains).map(v=>[v.id,`${v.number} · ${v.id}`]):Object.values(n.edges).map(v=>[v.id,`${v.id} · ${n.stations[n.nodes[v.from].stationId].name} → ${n.stations[n.nodes[v.to].stationId].name}`]);
  async function submit(e:FormEvent<HTMLFormElement>){e.preventDefault();const f=new FormData(e.currentTarget);const body={label:f.get('label'),description:f.get('description'),category:f.get('category'),severity:f.get('severity'),targetType:type,targetId:f.get('targetId'),effect:f.get('effect')};if(await action('/incidents',body))close();}
  return <Modal title="Registrar ocorrência" close={close}><p className="muted">O alerta recebe um rótulo e é propagado pela rede. Riscos por integração de passageiros são sinalizados sem bloquear automaticamente as linhas vizinhas.</p><form className="form-grid" onSubmit={submit}>
    <label className="span-2">Rótulo do alerta<input name="label" placeholder="Ex.: Falha de sinalização em Brás" required maxLength={120}/></label>
    <label>Tipo de local<select value={type} onChange={e=>setType(e.target.value)}><option value="LINE">Linha</option><option value="STATION">Estação</option><option value="EDGE">Trecho de via</option><option value="TRAIN">Composição</option></select></label>
    <label>Local afetado<select name="targetId" key={type} defaultValue={target?.id}>{targets.map(([id,name])=><option key={id} value={id}>{name}</option>)}</select></label>
    <label>Severidade<select name="severity" defaultValue="HIGH">{Object.entries(severity).map(([id,name])=><option key={id} value={id}>{name}</option>)}</select></label>
    <label>Efeito operacional<select name="effect" defaultValue="BLOCK">{Object.entries(effect).map(([id,name])=><option key={id} value={id}>{name}</option>)}</select></label>
    <label className="span-2">Categoria<select name="category"><option>Sinalização</option><option>Energia</option><option>Material rodante</option><option>Via permanente</option><option>Estação</option><option>Operação</option><option>Outros</option></select></label>
    <label className="span-2">Descrição<textarea name="description" rows={3} maxLength={2000} required placeholder="Descreva a situação e as medidas em andamento."/></label>
    <div className="dialog-actions span-2"><button type="button" onClick={close}>Cancelar</button><button className="danger" disabled={busy}>Registrar e propagar alerta</button></div>
  </form></Modal>;
}
