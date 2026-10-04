import {describe,it,expect} from 'vitest';
import {duration,position,visibleTrains} from './utils';
import type {Network,Train} from './types';
const train={id:'T1',number:'8501',model:'Teste',currentLineId:'L1',nodeId:null,edgeId:'E',progressMeters:50} as Train;
const network={stations:{A:{name:'Sé',x:0,y:10},B:{name:'Luz',x:100,y:50}},nodes:{A:{stationId:'A'},B:{stationId:'B'}},edges:{E:{from:'A',to:'B',lengthMeters:100}},trains:{T1:train}} as unknown as Network;
describe('mapa e identificação',()=>{
  it('interpola a posição pela distância percorrida',()=>expect(position(network,train)).toEqual([50,30]));
  it('limita visualização à extensão do trecho',()=>expect(position(network,{...train,progressMeters:300})).toEqual([100,50]));
  it('mantém posição na estação durante parada',()=>expect(position(network,{...train,nodeId:'A',edgeId:null})).toEqual([0,10]));
  it('busca por composição, estação e linha atual',()=>{expect(visibleTrains(network,'L1','8501')).toHaveLength(1);expect(visibleTrains(network,'L1','luz')).toHaveLength(1);expect(visibleTrains(network,'L2','')).toHaveLength(0);});
  it('exibe horas além de um dia sem reiniciar o relógio',()=>expect(duration(90061)).toBe('25:01:01'));
});
