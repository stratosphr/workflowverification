/*  $Id: chr_messages.pl,v 1.2 2006-06-13 10:33:54 matsc Exp $

    Part of CHR (Constraint Handling Rules)

    Author:        Jan Wielemaker and Tom Schrijvers
    E-mail:        Tom.Schrijvers@cs.kuleuven.be
    WWW:           http://www.swi-prolog.org
    Copyright (C): 2003-2004, K.U. Leuven

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    As a special exception, if you link this library with other files,
    compiled with a Free Software compiler, to produce an executable, this
    library does not by itself cause the resulting executable to be covered
    by the GNU General Public License. This exception does not however
    invalidate any other reasons why the executable file might be covered by
    the GNU General Public License.
*/

/*
Porting necessary
Currently ignored
*/

:- module(chr_messages,
	  [ chr_message/3		% +CHR Message, Out, Rest
	  ]).

%	compiler messages

chr_message(AnyMessage) --> [ AnyMessage ].

/*
Possible messages: see chr_message.pl in the SWI distribution
*/

