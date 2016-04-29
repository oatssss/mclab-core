% The following function computes the Euclidean distance between two vectors X and Y.
%
% Input:   Both X and Y should be COLUMN vectors of SAME dimensions (nxd) and (nxd).
%				'kSize' is a scalar for the kernel size.
%
% Output: 'val' contains the Euclidean distance
%
% Default:  kSize = 1.
%
% Comments: The code uses Incomplete Cholesky Decomposition.
%
% Author: Sohan Seth (sohan@cnel.ufl.edu)	Date: 11.03.2008

function val = d_ed(X,Y,kSize)

if nargin == 2
	kSize = 1;
end

n = size(X,1);

G =  incompleteCholeskySigma([X;Y],kSize);
OZ = [ones(n,1);zeros(n,1)]/n;
ZO = [zeros(n,1);ones(n,1)]/n;
val = (OZ'*G)*(G'*OZ) + (ZO'*G)*(G'*ZO) - 2*(ZO'*G)*(G'*OZ);

% ~ Done