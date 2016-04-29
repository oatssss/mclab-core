function [co] = DKL_CCE_HShannon_initialization(mult,post_init)
%function [co] = DKL_CCE_HShannon_initialization(mult)
%function [co] = DKL_CCE_HShannon_initialization(mult,post_init)
%Initialization of the "meta" Kullback-Leibler divergence estimator using the relation: D(f_1,f_2) = CE(f_1,f_2) - H(f_1). Here D denotes the Kullback-Leibler divergence, CE stands for cross-entropy and H is the Shannon differential entropy.
%
%Note:
%   1)The estimator is treated as a cost object (co).
%   2)We use the naming convention 'D<name>_initialization' to ease embedding new divergence estimation methods.
%   3)This is a meta method: the cross-entropy and Shannon differential entropy estimators can be arbitrary.
%
%INPUT:
%   mult: is a multiplicative constant relevant (needed) in the estimation; '=1' means yes (='exact' estimation), '=0' no (=estimation up to 'proportionality').
%   post_init: {field_name1,field_value1,field_name2,field_value2,...}; cell array containing the names and the values of the cost object fields that are to be used
%   (instead of their default values). For further details, see 'post_initialization.m'.
%OUTPUT:
%   co: cost object (structure).

%Copyright (C) 2012- Zoltan Szabo ("http://www.gatsby.ucl.ac.uk/~szabo/", "zoltan (dot) szabo (at) gatsby (dot) ucl (dot) ac (dot) uk")
%
%This file is part of the ITE (Information Theoretical Estimators) toolbox.
%
%ITE is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
%the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
%
%This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
%MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
%
%You should have received a copy of the GNU General Public License along with ITE. If not, see <http://www.gnu.org/licenses/>.

%mandatory fields (following the template structure of the estimators to make uniform usage of the estimators possible):
    co.name = 'KL_CCE_HShannon';
    co.mult = mult;
    
%other fields:
    co.CE_member_name = 'CE_kNN_k'; %you can change it to any cross-entropy estimator
    co.H_member_name = 'Shannon_kNN_k'; %you can change it to any Shannon entropy estimator

%post initialization (put it _before_ initialization of the members in case of a meta estimator):    
    if nargin==2 %there are given (name,value) cost object fields
        co = post_initialization(co,post_init);
    end  
    
%initialization of the member(s):
    co.CE_member_co = C_initialization(co.CE_member_name,1);%mult=1
    co.H_member_co = H_initialization(co.H_member_name,1);%mult=1
